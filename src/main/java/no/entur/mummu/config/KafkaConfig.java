package no.entur.mummu.config;

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
public class KafkaConfig {}
