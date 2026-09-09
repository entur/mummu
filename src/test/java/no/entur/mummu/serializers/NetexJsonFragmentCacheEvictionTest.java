package no.entur.mummu.serializers;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import no.entur.mummu.services.NetexEntitiesIndexLoader;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.StopPlace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Weak keys alone bound nothing: the index holds a strong reference to every
 * entity, so no fragment is ever collected while its entity is indexed, and the
 * cache would grow to the whole working set. These tests pin the byte cap that
 * keeps room for the NeTEx index itself and for serializing what is not cached.
 */
class NetexJsonFragmentCacheEvictionTest {

    private NetexEntitiesIndex index;
    private NetexJsonObjectMapper objectMapper;
    private MeterRegistry meterRegistry;

    @BeforeEach
    void setup() throws IOException {
        NetexEntitiesIndexLoader loader =
                new NetexEntitiesIndexLoader("src/test/resources/IntegrationTestFixture.xml.zip");
        index = loader.getNetexEntitiesIndex();
        objectMapper = new NetexJsonObjectMapper(new CustomSerializers(new MummuSerializerContext(loader)));
        meterRegistry = new SimpleMeterRegistry();
    }

    private List<StopPlace> stopPlaces() {
        return new ArrayList<>(index.getStopPlaceIndex().getLatestVersions());
    }

    private NetexJsonFragmentCache cacheCappedAt(long maxBytes) {
        return new NetexJsonFragmentCache(objectMapper, meterRegistry, maxBytes);
    }

    @Test
    void retainsNoMoreThanTheConfiguredCap() {
        long cap = 4096;
        NetexJsonFragmentCache cache = cacheCappedAt(cap);

        List<StopPlace> all = stopPlaces();
        long uncapped = 0;
        for (StopPlace stopPlace : all) {
            uncapped += cache.fragment(stopPlace).length;
        }

        assertTrue(uncapped > cap,
                "fixture precondition: the fragments together must exceed the cap, was " + uncapped);
        assertTrue(cache.retainedBytes() <= cap,
                "cache retained " + cache.retainedBytes() + " bytes, over the " + cap + " byte cap");
    }

    @Test
    void stillServesCorrectBytesForEntitiesThatWereEvicted() throws Exception {
        NetexJsonFragmentCache cache = cacheCappedAt(4096);

        List<StopPlace> all = stopPlaces();
        all.forEach(cache::fragment);

        // Whatever was evicted must simply be rendered again, identically.
        for (StopPlace stopPlace : all) {
            assertArrayEquals(objectMapper.get().writeValueAsBytes(stopPlace), cache.fragment(stopPlace));
        }
    }

    @Test
    void reportsWhatItRetains() {
        NetexJsonFragmentCache cache = cacheCappedAt(64L * 1024 * 1024);

        assertEquals(0, cache.retainedBytes(), "nothing rendered yet");

        long rendered = 0;
        for (StopPlace stopPlace : stopPlaces()) {
            rendered += cache.fragment(stopPlace).length;
        }

        assertEquals(rendered, cache.retainedBytes());
        // The entry count is published by GuavaCacheMetrics rather than by an
        // accessor of our own.
        assertEquals(stopPlaces().size(),
                (long) meterRegistry.get("cache.size").tag("cache", "mummu.fragment.cache").gauge().value());
    }

    @Test
    void publishesCacheSizeAndBytesAsMetrics() {
        NetexJsonFragmentCache cache = cacheCappedAt(64L * 1024 * 1024);
        stopPlaces().forEach(cache::fragment);

        assertNotNull(meterRegistry.find("mummu.fragment.cache.bytes").gauge(),
                "no gauge for the bytes the cache retains");
        assertEquals(cache.retainedBytes(),
                (long) meterRegistry.find("mummu.fragment.cache.bytes").gauge().value());
    }
}
