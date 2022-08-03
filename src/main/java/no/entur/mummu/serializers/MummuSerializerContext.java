package no.entur.mummu.serializers;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.ZoneId;

@Component
public class MummuSerializerContext implements Serializable {
    private ZoneId zoneId;

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }
}
