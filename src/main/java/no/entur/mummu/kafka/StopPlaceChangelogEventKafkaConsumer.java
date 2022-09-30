package no.entur.mummu.kafka;

import no.entur.mummu.updater.StopPlacesUpdater;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Profile("!test")
@Component
public class StopPlaceChangelogEventKafkaConsumer {

    @Autowired
    StopPlacesUpdater stopPlacesUpdater;

    @KafkaListener(
            topicPartitions = @TopicPartition(topic = "ror-stop-place-changelog-dev",
                    partitionOffsets = {
                            @PartitionOffset(partition = "0", initialOffset = "0"),
                            @PartitionOffset(partition = "1", initialOffset = "0"),
                            @PartitionOffset(partition = "2", initialOffset = "0"),
                            @PartitionOffset(partition = "3", initialOffset = "0"),
                            @PartitionOffset(partition = "4", initialOffset = "0"),
                            @PartitionOffset(partition = "5", initialOffset = "0"),
                            @PartitionOffset(partition = "6", initialOffset = "0"),
                            @PartitionOffset(partition = "7", initialOffset = "0"),
                            @PartitionOffset(partition = "8", initialOffset = "0"),
                            @PartitionOffset(partition = "9", initialOffset = "0"),
                            @PartitionOffset(partition = "10", initialOffset = "0"),
                            @PartitionOffset(partition = "11", initialOffset = "0"),
                            @PartitionOffset(partition = "12", initialOffset = "0"),
                            @PartitionOffset(partition = "13", initialOffset = "0"),
                            @PartitionOffset(partition = "14", initialOffset = "0")
                    }),
            containerFactory = "filterKafkaListenerContainerFactory")
    public void consume(@Payload ConsumerRecord<String, StopPlaceChangelogEvent> message) {
        stopPlacesUpdater.receiveStopPlaceUpdate(message.value());
    }
}
