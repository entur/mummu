package no.entur.mummu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@Configuration
@Profile("!test")
public class KafkaConfig {}
