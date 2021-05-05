package org.entur.mummu.config;

import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntityIndexReadOnlyView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
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
