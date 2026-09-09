package no.entur.mummu.serializers;

import no.entur.mummu.MummuApplication;
import no.entur.mummu.services.NetexEntitiesIndexLoader;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;

@ActiveProfiles("test")
@SpringBootTest(classes = MummuApplication.class)
class NetexJsonFragmentCacheTest {

    @Autowired
    private NetexJsonFragmentCache cache;

    @Autowired
    private NetexJsonObjectMapper netexJsonObjectMapper;

    @Autowired
    private NetexEntitiesIndexLoader loader;

    private StopPlace aStopPlace() {
        return loader.getNetexEntitiesIndex().getStopPlaceIndex().getLatestVersions().iterator().next();
    }

    private Quay aQuay() {
        return loader.getNetexEntitiesIndex().getQuayIndex().getLatestVersions().iterator().next();
    }

    @Test
    void stopPlaceFragmentIsByteIdenticalToObjectMapperOutput() throws Exception {
        StopPlace stopPlace = aStopPlace();
        assertArrayEquals(netexJsonObjectMapper.get().writeValueAsBytes(stopPlace), cache.fragment(stopPlace));
    }

    @Test
    void quayFragmentIsByteIdenticalToObjectMapperOutput() throws Exception {
        Quay quay = aQuay();
        assertArrayEquals(netexJsonObjectMapper.get().writeValueAsBytes(quay), cache.fragment(quay));
    }

    @Test
    void serializesEachEntityOnlyOnce() {
        StopPlace stopPlace = aStopPlace();
        byte[] first = cache.fragment(stopPlace);
        assertNotNull(first);
        assertSame(first, cache.fragment(stopPlace));
    }

    @Test
    void reserializesWhenTheIndexReplacesTheEntityInstance() {
        StopPlace original = aStopPlace();
        byte[] first = cache.fragment(original);

        // A changelog update parses a new object tree into the index, so the
        // updated stop place is a different instance even at the same version.
        StopPlace replacement = new StopPlace();
        replacement.setId(original.getId());
        replacement.setVersion(original.getVersion());

        byte[] second = cache.fragment(replacement);

        assertNotSame(first, second);
        assertArrayEquals(first, cache.fragment(original));
    }
}
