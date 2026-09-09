package no.entur.mummu.serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

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
 */
@Component
public class NetexJsonFragmentCache {

    private final ObjectMapper objectMapper;
    private final Cache<EntityInVersionStructure, byte[]> fragments = CacheBuilder.newBuilder()
            .weakKeys()
            .build();

    public NetexJsonFragmentCache(NetexJsonObjectMapper netexJsonObjectMapper) {
        this.objectMapper = netexJsonObjectMapper.get();
    }

    public byte[] fragment(EntityInVersionStructure entity) {
        try {
            return fragments.get(entity, () -> objectMapper.writeValueAsBytes(entity));
        } catch (ExecutionException e) {
            throw new IllegalStateException("Could not serialize " + entity.getId(), e.getCause());
        }
    }

    /**
     * How many entities currently have a rendered fragment retained. The heap
     * this cache holds is the main cost of pre-rendering, so it is worth being
     * able to observe it.
     */
    public long size() {
        return fragments.size();
    }
}
