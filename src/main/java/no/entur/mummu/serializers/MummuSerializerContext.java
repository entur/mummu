package no.entur.mummu.serializers;

import no.entur.mummu.services.NetexEntitiesIndexLoader;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.SiteFrame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.TimeZone;

@Component
public class MummuSerializerContext implements Serializable {
    private ZoneId zoneId;

    @Autowired
    public MummuSerializerContext(NetexEntitiesIndexLoader netexEntitiesIndexLoader) {
        configureSerializerContextWithTimeZone(netexEntitiesIndexLoader.getNetexEntitiesIndex());
    }

    public ZoneId getZoneId() {
        return zoneId;
    }

    private void configureSerializerContextWithTimeZone(NetexEntitiesIndex netexEntitiesIndex) {
        SiteFrame siteFrame = netexEntitiesIndex.getSiteFrames().stream()
                .findFirst().orElse(null);

        if (siteFrame != null && siteFrame.getFrameDefaults() != null && siteFrame.getFrameDefaults().getDefaultLocale() != null && siteFrame.getFrameDefaults().getDefaultLocale().getTimeZone() != null) {
            zoneId = TimeZone.getTimeZone(
                    siteFrame.getFrameDefaults().getDefaultLocale().getTimeZone()
            ).toZoneId();
        }
    }
}
