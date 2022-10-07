package no.entur.mummu.updater;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.entur.mummu.repositories.StopPlaceRepository;
import no.entur.mummu.services.NetexObjectFactory;
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
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.SiteRefStructure;
import org.rutebanken.netex.model.StopPlace;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
class StopPlacesUpdaterTest {

    private StopPlacesUpdater stopPlacesUpdater;

    private NetexEntitiesIndex netexEntitiesIndex;

    @Mock
    private StopPlaceRepository stopPlaceRepository;

    private NetexObjectFactory netexObjectFactory = new NetexObjectFactory();

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
        event.setStopPlaceChanged(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2005-01-01T03:00:00Z")));
        event.setStopPlaceVersion(72);

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
    void testDeleteMultimodal() {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.DELETE);
        event.setStopPlaceId("NSR:StopPlace:59687");
        event.setStopPlaceChanged(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2005-01-01T03:00:00Z")));
        event.setStopPlaceVersion(72);

        Assertions.assertEquals(1, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:59687").size());
        Assertions.assertEquals(1, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:44550").size());

        Assertions.assertNotNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:59687"));
        Assertions.assertNotNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:44550"));

        stopPlacesUpdater.receiveStopPlaceUpdate(event);

        Assertions.assertEquals(0, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:59687").size());
        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:59687"));

        Assertions.assertEquals(0, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:44550").size());
        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:44550"));
    }

    @Test
    void testUpdate() {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.UPDATE);
        event.setStopPlaceId("NSR:StopPlace:4004");
        event.setStopPlaceChanged(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2006-01-01T03:00:00Z")));
        event.setStopPlaceVersion(73);

        var stopPlace = netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4004");
        var allVersions = netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4004");

        var subject = cloneStopPlace(stopPlace)
                .withVersion("73")
                .withDescription(new MultilingualString().withValue("Changed!").withLang("en"));

        var update = new StopPlaceUpdate();
        var newVersions = new ArrayList<>(List.of(subject));
        newVersions.addAll(allVersions);
        update.setVersions(Map.of("NSR:StopPlace:4004", newVersions));
        update.setParkingVersions(Map.of());
        update.setQuayVersions(Map.of());

        Mockito.when(stopPlaceRepository.getStopPlaceUpdate("NSR:StopPlace:4004")).thenReturn(update);

        Assertions.assertEquals(2, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4004").size());

        stopPlacesUpdater.receiveStopPlaceUpdate(event);

        Assertions.assertEquals(3, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4004").size());
        Assertions.assertEquals("Changed!", netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4004").getDescription().getValue());
    }

    @Test
    void testCreate() {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.CREATE);
        event.setStopPlaceId("NSR:StopPlace:4005");
        event.setStopPlaceChanged(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2005-01-01T03:00:00Z")));
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
        update.setVersions(Map.of("NSR:StopPlace:4005", List.of(stopPlace)));
        update.setQuayVersions(Map.of(quay.getId(), List.of(quay)));
        update.setParkingVersions(Map.of(parking.getId(), List.of(parking)));

        Mockito.when(stopPlaceRepository.getStopPlaceUpdate(stopPlace.getId())).thenReturn(update);

        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(stopPlace.getId()));

        stopPlacesUpdater.receiveStopPlaceUpdate(event);

        Assertions.assertNotNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(stopPlace.getId()));
        Assertions.assertNotNull(netexEntitiesIndex.getQuayIndex().getLatestVersion(quay.getId()));
        Assertions.assertNotNull(netexEntitiesIndex.getParkingIndex().getLatestVersion(parking.getId()));
    }

    private StopPlace cloneStopPlace(StopPlace stopPlace) {
        try {
            var marshaller = JAXBContext
                    .newInstance(PublicationDeliveryStructure.class)
                    .createMarshaller();
            var unmarshaller = JAXBContext
                    .newInstance(PublicationDeliveryStructure.class)
                    .createUnmarshaller();
            var outputStream = new ByteArrayOutputStream();
            marshaller.marshal(netexObjectFactory.createStopPlace(stopPlace), outputStream);
            InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
            JAXBElement<StopPlace> element = (JAXBElement<StopPlace>) unmarshaller.unmarshal(inputStream);
            return element.getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testEventDiscardedIfChangedBeforePublicationTime() {
        Assertions.assertTrue(
                stopPlacesUpdater.filter(createTestSubject("2004-12-31T20:00:00Z"))
        );
    }

    @Test
    void testEventNotDiscardedIfChangedAfterPublicationTime() {
        Assertions.assertFalse(
                stopPlacesUpdater.filter(createTestSubject("2021-05-05T03:00:00Z"))
        );
    }

    @Test
    void testEventDiscardedIfChangedNull() {
        Assertions.assertTrue(
                stopPlacesUpdater.filter(createTestSubject(null))
        );
    }

    private static StopPlaceChangelogEvent createTestSubject(String changed) {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.UPDATE);
        event.setStopPlaceId("NSR:StopPlace:9999");
        event.setStopPlaceChanged(Optional.ofNullable(changed).map(v -> Instant.from(DateTimeFormatter.ISO_INSTANT.parse(v))).orElse(null));
        event.setStopPlaceVersion(1);
        return event;
    }
}
