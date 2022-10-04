package no.entur.mummu.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class StopPlaceChangelogEventRecordFilterStrategy implements RecordFilterStrategy<String, StopPlaceChangelogEvent> {

    private static final String DEFAULT_TIME_ZONE = "Europe/Oslo";

    private final NetexEntitiesIndex netexEntitiesIndex;

    public StopPlaceChangelogEventRecordFilterStrategy(NetexEntitiesIndex netexEntitiesIndex) {
        this.netexEntitiesIndex = netexEntitiesIndex;
    }

    @Override
    public boolean filter(ConsumerRecord<String, StopPlaceChangelogEvent> consumerRecord) {
        if (consumerRecord.value().getStopPlaceChanged() == null) {
            return true;
        }

        var changedTime = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(consumerRecord.value().getStopPlaceChanged()));
        var localPublicationTimestamp = netexEntitiesIndex.getPublicationTimestamp();
        var timeZone = netexEntitiesIndex.getSiteFrames().stream()
                .findFirst()
                .map(frame -> frame.getFrameDefaults().getDefaultLocale().getTimeZone())
                .orElse(DEFAULT_TIME_ZONE);
        var publicationTime = localPublicationTimestamp.atZone(ZoneId.of(timeZone)).toInstant();
        return changedTime.isBefore(publicationTime);
    }
}
