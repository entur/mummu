package no.entur.mummu.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.GuavaCacheMetrics;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Renders each NeTEx entity to JSON once and hands out the resulting bytes on
 * every later request for it.
 * <p>
 * List responses are a plain concatenation of independent per-entity documents,
 * so serving them from cached fragments produces byte-identical output while
 * turning the request-time cost into a memory copy.
 * <p>
 * Keys are weak and compared by identity, which is what keeps the cache honest
 * without any explicit invalidation: the changelog updater parses a new object
 * tree into the index, so an updated entity is a different instance and misses
 * the cache, while the instance it replaced becomes unreachable and has its
 * fragment collected along with it.
 * <p>
 * Weak keys bound nothing on their own, though. The index holds a strong
 * reference to every entity it serves, so a fragment is only collected once its
 * entity leaves the index, and Guava does not evict under memory pressure.
 * Uncapped, the cache would grow to the whole working set — around 292 MB on a
 * full dataset, on top of the ~1.9 GB the index itself retains. The cap below
 * is what leaves room for the index and for serializing responses that are not
 * cached. Evicting is always safe: an evicted fragment is simply rendered again.
 * <p>
 * The cap is a byte budget rather than an entry count because fragment sizes
 * span four orders of magnitude, from a 0.36 KB quay to a topographic place
 * whose polygon geometry reaches tens of MB.
 */
@Component
public class NetexJsonFragmentCache {

    private final ObjectMapper objectMapper;
    private final Cache<EntityInVersionStructure, byte[]> fragments;
    private final AtomicLong retainedBytes = new AtomicLong();

    public NetexJsonFragmentCache(
            NetexJsonObjectMapper netexJsonObjectMapper,
            MeterRegistry meterRegistry,
            @Value("${no.entur.mummu.fragment-cache.max-bytes:268435456}") long maxBytes
    ) {
        this.objectMapper = netexJsonObjectMapper.get();
        this.fragments = CacheBuilder.newBuilder()
                .weakKeys()
                .maximumWeight(maxBytes)
                .weigher((EntityInVersionStructure entity, byte[] fragment) -> fragment.length)
                .removalListener(notification -> {
                    byte[] fragment = (byte[]) notification.getValue();
                    if (fragment != null) {
                        retainedBytes.addAndGet(-fragment.length);
                    }
                })
                .recordStats()
                .build();

        // Hits, misses, evictions and load timings. The byte gauge is the one
        // that answers how much heap this is actually holding.
        GuavaCacheMetrics.monitor(meterRegistry, fragments, "mummu.fragment.cache");
        Gauge.builder("mummu.fragment.cache.bytes", retainedBytes, AtomicLong::get)
                .description("Bytes of pre-rendered NeTEx JSON currently retained")
                .baseUnit("bytes")
                .register(meterRegistry);
        Gauge.builder("mummu.fragment.cache.max.bytes", () -> maxBytes)
                .description("Configured ceiling on retained pre-rendered NeTEx JSON")
                .baseUnit("bytes")
                .register(meterRegistry);
    }

    public byte[] fragment(EntityInVersionStructure entity) {
        try {
            return fragments.get(entity, () -> {
                byte[] fragment = objectMapper.writeValueAsBytes(entity);
                retainedBytes.addAndGet(fragment.length);
                return fragment;
            });
        } catch (ExecutionException e) {
            throw new IllegalStateException("Could not serialize " + entity.getId(), e.getCause());
        }
    }

    /** The heap those fragments hold, which is what the cap bounds. */
    public long retainedBytes() {
        return retainedBytes.get();
    }
}
