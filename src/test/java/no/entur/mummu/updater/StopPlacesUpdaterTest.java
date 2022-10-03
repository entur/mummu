package no.entur.mummu.updater;

import no.entur.mummu.repositories.StopPlaceRepository;
import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.rutebanken.irkalla.avro.EnumType;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.rutebanken.netex.model.EntityStructure;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.SiteRefStructure;
import org.rutebanken.netex.model.StopPlace;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
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

    @Test
    void testUpdate() {

    }

    @Test
    void testCreate() {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.CREATE);
        event.setStopPlaceId("NSR:StopPlace:4005");
        event.setStopPlaceChanged("2005-01-01T03:00:00Z");
        event.setStopPlaceVersion(1);

        var quay = new Quay()
                .withId("NSR:Quay:9999")
                .withVersion("1");

        var stopPlace = new StopPlace()
                .withId("NSR:StopPlace:4005")
                .withVersion("1")
                .withQuays(new Quays_RelStructure().withQuayRefOrQuay(quay));

        var parking = new Parking()
                .withId("NSR:Parking:9999")
                .withVersion("1")
                .withParentSiteRef(new SiteRefStructure().withRef(stopPlace.getId()).withVersion(stopPlace.getVersion()));

        var update = new StopPlaceUpdate();
        update.setVersions(List.of(stopPlace));
        update.setQuayVersions(Map.of(quay.getId(), List.of(quay)));
        update.setParkingVersions(Map.of(parking.getId(), List.of(parking)));

        Mockito.when(stopPlaceRepository.getStopPlaceUpdate(stopPlace.getId())).thenReturn(update);

        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(stopPlace.getId()));

        stopPlacesUpdater.receiveStopPlaceUpdate(event);

        Assertions.assertNotNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(stopPlace.getId()));
        Assertions.assertNotNull(netexEntitiesIndex.getQuayIndex().getLatestVersion(quay.getId()));
        Assertions.assertNotNull(netexEntitiesIndex.getParkingIndex().getLatestVersion(parking.getId()));
    }
}
