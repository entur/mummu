package no.entur.mummu.config;

import no.entur.mummu.services.NetexEntitiesIndexLoader;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.helper.stopplace.changelog.kafka.PublicationTimeRecordFilterStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.time.ZoneId;

@Configuration
@ConditionalOnProperty(
        name = "no.entur.mummu.stopplacesupdater.enabled",
        havingValue = "true"
)
public class StopPlaceChangelogFilterConfig {
    
    private static final String DEFAULT_TIME_ZONE = "Europe/Oslo";
    
    @Bean("publicationTimeRecordFilterStrategy")
    public PublicationTimeRecordFilterStrategy publicationTimeRecordFilterStrategy(
            NetexEntitiesIndexLoader netexEntitiesIndexLoader) {
        NetexEntitiesIndex netexEntitiesIndex = netexEntitiesIndexLoader.getNetexEntitiesIndex();
        Instant publicationTime = getPublicationTime(netexEntitiesIndex);
        return new PublicationTimeRecordFilterStrategy(publicationTime);
    }
    
    private Instant getPublicationTime(NetexEntitiesIndex netexEntitiesIndex) {
        var localPublicationTimestamp = netexEntitiesIndex.getPublicationTimestamp();
        var timeZone = netexEntitiesIndex.getSiteFrames().stream()
                .findFirst()
                .map(frame -> frame.getFrameDefaults().getDefaultLocale().getTimeZone())
                .orElse(DEFAULT_TIME_ZONE);
        return localPublicationTimestamp.atZone(ZoneId.of(timeZone)).toInstant();
    }
}