package no.entur.mummu.serializers;

import no.entur.mummu.services.NetexEntitiesIndexLoader;
import no.entur.mummu.updater.StopPlacesUpdater;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.helper.stopplace.changelog.StopPlaceChangelog;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The fragment cache carries no explicit invalidation: it relies on a changelog
 * update parsing a fresh object tree into the index, so the updated entity is a
 * different instance and misses the identity-keyed cache. If the parser were
 * ever to mutate entities in place instead, cached fragments would go stale —
 * which is what these tests pin down.
 */
@ExtendWith(MockitoExtension.class)
class NetexJsonFragmentCacheUpdateTest {

    private static final String UPDATED_STOP_PLACE = "NSR:StopPlace:337";
    private static final String UPDATED_QUAY = "NSR:Quay:100148";

    @Mock
    private StopPlaceChangelog stopPlaceChangelog;

    private NetexEntitiesIndex index;
    private StopPlacesUpdater updater;
    private NetexJsonFragmentCache cache;
    private NetexJsonObjectMapper objectMapper;

    @BeforeEach
    void setup() throws IOException {
        NetexEntitiesIndexLoader loader =
                new NetexEntitiesIndexLoader("src/test/resources/no/entur/mummu/updater/UpdateBaseFixture.xml.zip");
        index = loader.getNetexEntitiesIndex();
        objectMapper = new NetexJsonObjectMapper(new CustomSerializers(new MummuSerializerContext(loader)));
        cache = new NetexJsonFragmentCache(objectMapper);
        updater = new StopPlacesUpdater(loader, stopPlaceChangelog);
    }

    private StopPlace latest() {
        return index.getStopPlaceIndex().getLatestVersion(UPDATED_STOP_PLACE);
    }

    private Quay latestQuay() {
        return index.getQuayIndex().getLatestVersion(UPDATED_QUAY);
    }

    private String fragmentText(StopPlace stopPlace) {
        return new String(cache.fragment(stopPlace), StandardCharsets.UTF_8);
    }

    @Test
    void doesNotServeAStaleFragmentAfterAChangelogUpdate() throws Exception {
        // Render the fragment before the update, so a stale entry exists.
        assertFalse(fragmentText(latest()).contains("\"test\""),
                "fixture precondition: the description is not yet 'test'");

        updater.onStopPlaceUpdated(
                "NSR:StopPlace:59872",
                getClass().getClassLoader().getResourceAsStream("no/entur/mummu/updater/UpdateMultiModalFixture.xml"));

        StopPlace updated = latest();
        assertEquals("test", updated.getDescription().getValue(), "precondition: the update landed in the index");
        assertTrue(fragmentText(updated).contains("\"test\""), "the cache served a stale fragment");
        assertArrayEquals(objectMapper.get().writeValueAsBytes(updated), cache.fragment(updated));
    }

    @Test
    void doesNotServeAStaleQuayFragmentAfterAChangelogUpdate() throws Exception {
        Quay before = latestQuay();
        assertEquals("12", before.getVersion(), "fixture precondition: the quay starts at version 12");

        // Render the fragment before the update, so a stale entry exists.
        byte[] stale = cache.fragment(before);

        updater.onStopPlaceUpdated(
                "NSR:StopPlace:59872",
                getClass().getClassLoader().getResourceAsStream("no/entur/mummu/updater/UpdateMultiModalFixture.xml"));

        Quay updated = latestQuay();
        assertEquals("13", updated.getVersion(), "precondition: the update landed in the index");

        byte[] fresh = cache.fragment(updated);
        assertFalse(Arrays.equals(stale, fresh), "the cache served the fragment it rendered before the update");
        assertArrayEquals(objectMapper.get().writeValueAsBytes(updated), fresh);
    }
}
