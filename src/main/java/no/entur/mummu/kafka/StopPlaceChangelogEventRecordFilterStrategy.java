package no.entur.mummu.kafka;

import no.entur.mummu.services.StopPlacesUpdater;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class StopPlaceChangelogEventRecordFilterStrategy implements RecordFilterStrategy<String, StopPlaceChangelogEvent> {

    private static final Logger log = LoggerFactory.getLogger(StopPlaceChangelogEventRecordFilterStrategy.class);

    private final NetexEntitiesIndex netexEntitiesIndex;

    public StopPlaceChangelogEventRecordFilterStrategy(NetexEntitiesIndex netexEntitiesIndex) {
        this.netexEntitiesIndex = netexEntitiesIndex;
    }

    @Override
    public boolean filter(ConsumerRecord<String, StopPlaceChangelogEvent> consumerRecord) {
        var changed = Instant.from(DateTimeFormatter.ISO_INSTANT.parse(consumerRecord.value().getStopPlaceChanged()));

        // var publicationTimestamp = netexEntitiesIndex.getPublicationTimestamp();
        // get ZoneOffset from index as well?
        var publicationTimestamp = LocalDateTime.now();

        var result = changed.isBefore(publicationTimestamp.toInstant(ZoneOffset.of("+02:00")));
        log.info("filter result={}", result);
        return result;
    }
}
