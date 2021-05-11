package no.entur.mummu.config;

import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntityIndexReadOnlyView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class NetexEntityIndexConfiguration {
    private final NetexEntityIndexReadOnlyView netexEntityIndex;

    @Autowired
    public NetexEntityIndexConfiguration(@Value("${no.entur.mummu.data-file}") String dataFile) throws IOException {
        var parser = new NetexParser();
        netexEntityIndex = parser.parse(dataFile);
    }

    @Bean
    public NetexEntityIndexReadOnlyView netexEntityIndex() {
        return netexEntityIndex;
    }
}
