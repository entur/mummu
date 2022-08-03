package no.entur.mummu.serializers;

import org.springframework.stereotype.Component;

import java.time.ZoneId;

@Component
public class MummuSerializerContext {
    private ZoneId zoneId;

    public ZoneId getZoneId() {
        return zoneId;
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }
}
