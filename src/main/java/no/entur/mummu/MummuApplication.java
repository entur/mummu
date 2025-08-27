package no.entur.mummu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"no.entur.mummu", "org.rutebanken.helper.stopplace.changelog"})
public class MummuApplication {

    public static void main(String[] args) {
        SpringApplication.run(MummuApplication.class, args);
    }

}
