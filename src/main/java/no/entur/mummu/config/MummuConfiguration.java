package no.entur.mummu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.ForwardedHeaderFilter;

public class MummuConfiguration {

    @Bean
    ForwardedHeaderFilter forwardedHeaderFilter() {
        return new ForwardedHeaderFilter();
    }
}
