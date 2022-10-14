package no.entur.mummu.kafka;

import no.entur.mummu.updater.StopPlacesUpdater;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Profile("!test")
@EnableKafka
@Component
public class StopPlaceChangelogEventKafkaConsumer {

    @Autowired
    StopPlacesUpdater stopPlacesUpdater;

    @KafkaListener(
            topicPartitions = {
                    @TopicPartition(
                            topic = "${no.entur.mummu.changelog.topic:ror-stop-place-changelog-dev}",
                            partitions = {"#{@partitionFinder.allPartitions(\"${no.entur.mummu.changelog.topic:ror-stop-place-changelog-dev}\")}"}
                    )
            })
    public void consume(@Payload ConsumerRecord<String, StopPlaceChangelogEvent> message) {
        stopPlacesUpdater.receiveStopPlaceUpdate(message.value());
    }
}
