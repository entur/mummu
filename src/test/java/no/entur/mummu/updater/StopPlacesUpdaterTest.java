package no.entur.mummu.updater;

import no.entur.mummu.repositories.StopPlaceRepository;
import no.entur.mummu.services.NetexEntitiesIndexLoader;
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
import org.rutebanken.netex.model.Quay;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
class StopPlacesUpdaterTest {

    private StopPlacesUpdater stopPlacesUpdater;

    private NetexEntitiesIndex netexEntitiesIndex;

    @Mock
    private StopPlaceRepository stopPlaceRepository;

    @BeforeEach
    void setup() throws IOException {
        NetexEntitiesIndexLoader loader = new NetexEntitiesIndexLoader("src/test/resources/no/entur/mummu/updater/UpdateBaseFixture.xml.zip");
        netexEntitiesIndex = loader.getNetexEntitiesIndex();
        stopPlacesUpdater = new StopPlacesUpdater(loader, stopPlaceRepository);
    }

    @Test
    void testDelete() {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.DELETE);
        event.setStopPlaceId("NSR:StopPlace:4055");
        event.setStopPlaceChanged(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2022-10-16T20:00:00Z")));
        event.setStopPlaceVersion(4);

        Assertions.assertTrue(netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4055").size() > 0);
        Collection<String> quays = netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4055").getQuays().getQuayRefOrQuay()
                .stream()
                .map(o -> (Quay) o.getValue())
                .map(EntityStructure::getId)
                .collect(Collectors.toList());

        Assertions.assertTrue(
                quays.stream().map(id -> netexEntitiesIndex.getQuayIndex().getLatestVersion(id)).findAny().isPresent()
        );

        stopPlacesUpdater.receiveStopPlaceUpdate(event);

        Assertions.assertEquals(0, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4055").size());
        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4055"));
        quays.stream().map(id -> netexEntitiesIndex.getQuayIndex().getAllVersions(id)).forEach(versions -> Assertions.assertTrue(versions.isEmpty()));
        quays.stream().map(id -> netexEntitiesIndex.getQuayIndex().getLatestVersion(id)).forEach(Assertions::assertNull);
    }

    @Test
    void testDeleteMultimodal() {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.DELETE);
        event.setStopPlaceId("NSR:StopPlace:59872");
        event.setStopPlaceChanged(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2022-10-16T20:00:00Z")));
        event.setStopPlaceVersion(7);

        Assertions.assertEquals(7, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:59872").size());
        Assertions.assertEquals(6, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:337").size());

        Assertions.assertNotNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:59872"));
        Assertions.assertNotNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:337"));

        stopPlacesUpdater.receiveStopPlaceUpdate(event);

        Assertions.assertEquals(0, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:59872").size());
        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:59872"));

        Assertions.assertEquals(0, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:337").size());
        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:337"));
    }

    @Test
    void testUpdate() {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.UPDATE);
        event.setStopPlaceId("NSR:StopPlace:4055");
        event.setStopPlaceChanged(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2022-10-16T20:00:00Z")));
        event.setStopPlaceVersion(5);

        Mockito.when(stopPlaceRepository.getStopPlaceUpdate("NSR:StopPlace:4055")).thenReturn(getClass().getClassLoader().getResourceAsStream("no/entur/mummu/updater/UpdateFixture.xml"));

        Assertions.assertEquals(4, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4055").size());

        stopPlacesUpdater.receiveStopPlaceUpdate(event);

        Assertions.assertEquals(5, netexEntitiesIndex.getStopPlaceIndex().getAllVersions("NSR:StopPlace:4055").size());
        Assertions.assertEquals("test", netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:4055").getDescription().getValue());
    }

    @Test
    void testUpdateMultiModal() {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.UPDATE);
        event.setStopPlaceId("NSR:StopPlace:59872");
        event.setStopPlaceChanged(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2022-10-16T20:00:00Z")));
        event.setStopPlaceVersion(8);

        Mockito.when(stopPlaceRepository.getStopPlaceUpdate("NSR:StopPlace:59872")).thenReturn(getClass().getClassLoader().getResourceAsStream("no/entur/mummu/updater/UpdateMultiModalFixture.xml"));

        stopPlacesUpdater.receiveStopPlaceUpdate(event);

        Assertions.assertEquals("8", netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:59872").getVersion());
        Assertions.assertEquals("test", netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:337").getDescription().getValue());
    }

    @Test
    void testCreate() {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.CREATE);
        event.setStopPlaceId("NSR:StopPlace:62302");
        event.setStopPlaceChanged(Instant.from(DateTimeFormatter.ISO_INSTANT.parse("2022-10-16T20:00:00Z")));
        event.setStopPlaceVersion(1);

        Mockito.when(stopPlaceRepository.getStopPlaceUpdate("NSR:StopPlace:62302")).thenReturn(getClass().getClassLoader().getResourceAsStream("no/entur/mummu/updater/CreateFixture.xml"));

        Assertions.assertNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:62302"));

        stopPlacesUpdater.receiveStopPlaceUpdate(event);

        Assertions.assertNotNull(netexEntitiesIndex.getStopPlaceIndex().getLatestVersion("NSR:StopPlace:62302"));
        Assertions.assertNotNull(netexEntitiesIndex.getQuayIndex().getLatestVersion("NSR:Quay:107475"));
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
                stopPlacesUpdater.filter(createTestSubject("2022-10-17T03:00:00Z"))
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
