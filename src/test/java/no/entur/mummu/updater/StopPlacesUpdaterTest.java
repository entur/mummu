package no.entur.mummu.updater;

import no.entur.mummu.services.NetexEntitiesIndexLoader;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.helper.stopplace.changelog.StopPlaceChangelog;
import org.rutebanken.netex.model.EntityStructure;
import org.rutebanken.netex.model.Quay;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
class StopPlacesUpdaterTest {

    private StopPlacesUpdater stopPlacesUpdater;

    private NetexEntitiesIndex netexEntitiesIndex;

    @Mock
    private StopPlaceChangelog stopPlaceChangelog;

    @BeforeEach
    void setup() throws IOException {
        NetexEntitiesIndexLoader loader = new NetexEntitiesIndexLoader("src/test/resources/no/entur/mummu/updater/UpdateBaseFixture.xml.zip");
        netexEntitiesIndex = loader.getNetexEntitiesIndex();
        stopPlacesUpdater = new StopPlacesUpdater(loader, stopPlaceChangelog);
    }

    @Test
    void testDelete() {
        Assertions.assertTrue(netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4055").size() > 0);
        Collection<String> quays = netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4055").getQuays().getQuayRefOrQuay()
                .stream()
                .map(o -> (Quay) o)
                .map(EntityStructure::getId)
                .collect(Collectors.toList());

        Assertions.assertTrue(
                quays.stream().map(id -> netexEntitiesIndex.getQuayIndex().getLatestVersion(id)).findAny().isPresent()
        );

        stopPlacesUpdater.onStopPlaceDeleted("NSR:StopPlace:4055");

        Assertions.assertEquals(0, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4055").size());
        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4055"));
        quays.stream().map(id -> netexEntitiesIndex.getQuayIndex().getAllVersions(id)).forEach(versions -> Assertions.assertTrue(versions.isEmpty()));
        quays.stream().map(id -> netexEntitiesIndex.getQuayIndex().getLatestVersion(id)).forEach(Assertions::assertNull);
    }

    @Test
    void testDeleteMultimodal() {
        Assertions.assertEquals(7, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:59872").size());
        Assertions.assertEquals(6, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:337").size());

        Assertions.assertNotNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:59872"));
        Assertions.assertNotNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:337"));

        stopPlacesUpdater.onStopPlaceDeleted("NSR:StopPlace:59872");

        Assertions.assertEquals(0, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:59872").size());
        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:59872"));

        Assertions.assertEquals(0, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:337").size());
        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:337"));
    }

    @Test
    void testUpdate() {
        Assertions.assertEquals(4, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4055").size());

        stopPlacesUpdater.onStopPlaceCreated("NSR:StopPlace:4055", getClass().getClassLoader().getResourceAsStream("no/entur/mummu/updater/UpdateFixture.xml"));

        Assertions.assertEquals(5, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4055").size());
        Assertions.assertEquals("test", netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4055").getDescription().getValue());
    }

    @Test
    void testUpdateMultiModal() {
        stopPlacesUpdater.onStopPlaceUpdated("NSR:StopPlace:59872", getClass().getClassLoader().getResourceAsStream("no/entur/mummu/updater/UpdateMultiModalFixture.xml"));

        Assertions.assertEquals("8", netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:59872").getVersion());
        Assertions.assertEquals("test", netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:337").getDescription().getValue());
    }

    @Test
    void testCreate() {
        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:62302"));

        stopPlacesUpdater.onStopPlaceCreated("NSR:StopPlace:62302", getClass().getClassLoader().getResourceAsStream("no/entur/mummu/updater/CreateFixture.xml"));

        Assertions.assertNotNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:62302"));
        Assertions.assertNotNull(netexEntitiesIndex.getQuayIndex().getLatestVersion("NSR:Quay:107475"));
    }
}
