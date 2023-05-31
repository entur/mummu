package no.entur.mummu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MummuApplication.class),
})
public class MummuTestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MummuTestApplication.class, args);
    }
}
