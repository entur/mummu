package no.entur.mummu.updater;

import no.entur.mummu.repositories.StopPlaceRepository;
import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.rutebanken.irkalla.avro.EnumType;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.rutebanken.netex.model.EntityStructure;
import org.rutebanken.netex.model.Quay;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class StopPlacesUpdaterTest {

    private StopPlacesUpdater stopPlacesUpdater;

    private NetexEntitiesIndex netexEntitiesIndex;

    @Mock
    private StopPlaceRepository stopPlaceRepository;

    @BeforeEach
    void setup() throws IOException {
        NetexParser parser = new NetexParser();
        netexEntitiesIndex = parser.parse("src/test/resources/IntegrationTestFixture.xml.zip");
        stopPlacesUpdater = new StopPlacesUpdater(netexEntitiesIndex, stopPlaceRepository);
    }

    @Test
    void testDelete() {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.DELETE);
        event.setStopPlaceId("NSR:StopPlace:4004");
        event.setStopPlaceChanged("2005-01-01T03:00:00Z");
        event.setStopPlaceVersion(1);

        Assertions.assertTrue(netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4004").size() > 0);
        Collection<String> quays = netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4004").getQuays().getQuayRefOrQuay()
                .stream()
                .map(o -> (Quay) o)
                .map(EntityStructure::getId)
                .collect(Collectors.toList());

        Assertions.assertTrue(
                quays.stream().map(id -> netexEntitiesIndex.getQuayIndex().getLatestVersion(id)).findAny().isPresent()
        );

        stopPlacesUpdater.receiveStopPlaceUpdate(event);

        Assertions.assertEquals(0, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4004").size());
        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4004"));
        quays.stream().map(id -> netexEntitiesIndex.getQuayIndex().getAllVersions(id)).forEach(versions -> Assertions.assertTrue(versions.isEmpty()));
        quays.stream().map(id -> netexEntitiesIndex.getQuayIndex().getLatestVersion(id)).forEach(Assertions::assertNull);
    }
}
