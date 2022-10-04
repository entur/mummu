package no.entur.mummu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TiamatClientConfig {

    @Value("${no.entur.mummu.tiamat.url=https://api.dev.entur.io/stop-places/v1}")
    private String tiamatUrl;

    @Bean
    public RestTemplate tiamatClient(RestTemplateBuilder builder) {
        return builder
                .rootUri(tiamatUrl)
                .build();
    }
}
