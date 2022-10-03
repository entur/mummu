package no.entur.mummu.updater;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import no.entur.mummu.repositories.StopPlaceRepository;
import no.entur.mummu.serializers.CustomSerializers;
import no.entur.mummu.serializers.MummuSerializerContext;
import no.entur.mummu.services.NetexObjectFactory;
import org.apache.commons.lang3.SerializationUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.entur.mummu.services.NetexObjectFactory.NAMESPACE_URI;


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
        event.setStopPlaceChanged("2005-01-01T03:00:00Z");
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
    void testUpdate() throws JsonProcessingException {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.UPDATE);
        event.setStopPlaceId("NSR:StopPlace:4004");
        event.setStopPlaceChanged("2006-01-01T03:00:00Z");
        event.setStopPlaceVersion(73);

        var stopPlace = netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4004");
        var allVersions = netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4004");

        var subject = cloneStopPlace(stopPlace)
                .withVersion("73")
                .withDescription(new MultilingualString().withValue("Changed!").withLang("en"));

        var update = new StopPlaceUpdate();
        var newVersions = new ArrayList<>(List.of(subject));
        newVersions.addAll(allVersions);
        update.setVersions(newVersions);
        update.setParkingVersions(Map.of());
        update.setQuayVersions(Map.of());
        //var quays = stopPlace.getQuays().getQuayRefOrQuay().stream().map(q -> netexEntitiesIndex.getQuayIndex().getAllVersions(((Quay)q).getId()));
        //update.setQuayVersions(quays.collect(Collectors.toMap(e -> e.stream().findFirst().get().getId(), e -> e)));

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
}
