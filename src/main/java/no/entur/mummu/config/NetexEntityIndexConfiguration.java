package no.entur.mummu.config;

import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class NetexEntityIndexConfiguration {
    private final NetexEntitiesIndex netexEntitiesIndex;
    private static final Logger log = LoggerFactory.getLogger(NetexEntityIndexConfiguration.class);

    @Autowired
    public NetexEntityIndexConfiguration(@Value("${no.entur.mummu.data-file}") String dataFile) throws IOException {
        var parser = new NetexParser();
        long now = System.currentTimeMillis();
        netexEntitiesIndex = parser.parse(dataFile);
        log.info("Parsed NeTEx file in {} ms", System.currentTimeMillis() - now);
    }

    @Bean
    public NetexEntitiesIndex netexEntitiesIndex() {
        return netexEntitiesIndex;
    }
}
