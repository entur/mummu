package no.entur.mummu.config;

import no.entur.mummu.serializers.MummuSerializerContext;
import org.entur.netex.NetexParser;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.SiteFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.TimeZone;

@Configuration
public class NetexEntityIndexConfiguration {
    private final NetexEntitiesIndex netexEntitiesIndex;
    private static final Logger log = LoggerFactory.getLogger(NetexEntityIndexConfiguration.class);

    @Autowired
    public NetexEntityIndexConfiguration(@Value("${no.entur.mummu.data-file}") String dataFile, MummuSerializerContext mummuSerializerContext) throws IOException {
        var parser = new NetexParser();
        long now = System.currentTimeMillis();
        netexEntitiesIndex = parser.parse(dataFile);

        configureSerializerContextWithTimeZone(mummuSerializerContext);

        log.info("Parsed NeTEx file in {} ms", System.currentTimeMillis() - now);
    }

    private void configureSerializerContextWithTimeZone(MummuSerializerContext mummuSerializerContext) {
        SiteFrame siteFrame = netexEntitiesIndex.getSiteFrames().stream()
                        .findFirst().orElse(null);

        if (siteFrame != null && siteFrame.getFrameDefaults() != null && siteFrame.getFrameDefaults().getDefaultLocale() != null && siteFrame.getFrameDefaults().getDefaultLocale().getTimeZone() != null) {
            mummuSerializerContext.setZoneId(
                    TimeZone.getTimeZone(
                            siteFrame.getFrameDefaults().getDefaultLocale().getTimeZone()
                    ).toZoneId()
            );
        }
    }

    @Bean
    public NetexEntitiesIndex netexEntitiesIndex() {
        return netexEntitiesIndex;
    }
}
