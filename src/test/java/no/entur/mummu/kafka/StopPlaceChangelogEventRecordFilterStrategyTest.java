package no.entur.mummu.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.index.impl.NetexEntitiesIndexImpl;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.irkalla.avro.EnumType;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.rutebanken.netex.model.LocaleStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.VersionFrameDefaultsStructure;

import java.time.LocalDateTime;


class StopPlaceChangelogEventRecordFilterStrategyTest {

    private StopPlaceChangelogEventRecordFilterStrategy filterStrategy;

    @BeforeEach
    void setup() {
        NetexEntitiesIndex netexEntitiesIndex = new NetexEntitiesIndexImpl();
        netexEntitiesIndex.setPublicationTimestamp(LocalDateTime.of(2005, 1, 1, 2, 0, 0));
        var localeStructure = new LocaleStructure();
        localeStructure.setTimeZone("Europe/Oslo");
        netexEntitiesIndex.getSiteFrames().add(new SiteFrame().withFrameDefaults(new VersionFrameDefaultsStructure().withDefaultLocale(localeStructure)));
        filterStrategy = new StopPlaceChangelogEventRecordFilterStrategy(netexEntitiesIndex);
    }

    @Test
    void testEventDiscardedIfChangedBeforePublicationTime() {
        Assertions.assertTrue(
                filterStrategy.filter(createTestSubject("2004-12-31T20:00:00Z"))
        );
    }

    @Test
    void testEventNotDiscardedIfChangedAfterPublicationTime() {
        Assertions.assertFalse(
                filterStrategy.filter(createTestSubject("2005-01-01T03:00:00Z"))
        );
    }

    @NotNull
    private static ConsumerRecord<String, StopPlaceChangelogEvent> createTestSubject(String changed) {
        var event = new StopPlaceChangelogEvent();
        event.setEventType(EnumType.UPDATE);
        event.setStopPlaceId("NSR:StopPlace:9999");
        event.setStopPlaceChanged(changed);
        event.setStopPlaceVersion(1);
        var subject = new ConsumerRecord<String, StopPlaceChangelogEvent>("test", 0, 0, null, event);
        return subject;
    }


}
