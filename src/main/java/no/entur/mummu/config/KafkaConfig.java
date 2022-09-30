package no.entur.mummu.config;

import no.entur.mummu.kafka.StopPlaceChangelogEventRecordFilterStrategy;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@EnableKafka
@Configuration
@Profile("!test")
public class KafkaConfig {

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, StopPlaceChangelogEvent>
    filterKafkaListenerContainerFactory(ConsumerFactory<String, StopPlaceChangelogEvent> enturConsumerFactory, NetexEntitiesIndex netexEntitiesIndex) {
        ConcurrentKafkaListenerContainerFactory<String, StopPlaceChangelogEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(enturConsumerFactory);
        factory.setConcurrency(1);
        factory.setRecordFilterStrategy(new StopPlaceChangelogEventRecordFilterStrategy(netexEntitiesIndex));
        return factory;
    }
}
