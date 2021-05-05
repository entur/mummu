package org.entur.mummu;

import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntityIndexReadOnlyView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class TestNetexEntityIndexConfiguration {
    @Bean
    public NetexEntityIndexReadOnlyView netexEntityIndex() throws IOException {
        var parser = new NetexParser();
        return parser.parseFromZip("src/test/resources/IntegrationTestFixture.xml.zip");
    }
}
