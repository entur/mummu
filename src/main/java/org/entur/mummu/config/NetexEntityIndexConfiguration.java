package org.entur.mummu.config;

import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntityIndexReadOnlyView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("!test")
public class NetexEntityIndexConfiguration {
    private final NetexEntityIndexReadOnlyView netexEntityIndex;


    public NetexEntityIndexConfiguration() throws IOException {
        var parser = new NetexParser();
        netexEntityIndex = parser.parseFromZip("src/main/resources/CurrentwithServiceFrame_latest.zip");
    }

    @Bean
    public NetexEntityIndexReadOnlyView netexEntityIndex() {
        return netexEntityIndex;
    }
}
