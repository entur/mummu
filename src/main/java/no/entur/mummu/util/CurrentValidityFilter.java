package no.entur.mummu.util;

import org.rutebanken.netex.model.EntityInVersionStructure;

import java.time.Instant;
import java.time.ZoneId;
import java.util.function.Predicate;

public class CurrentValidityFilter implements Predicate<EntityInVersionStructure> {
    private final ZoneId zoneId;
    public CurrentValidityFilter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public boolean test(EntityInVersionStructure entity) {
        return entity.getValidBetween().stream()
                .filter(validBetween -> validBetween.getToDate() != null)
                .noneMatch(validBetween -> Instant.now().isAfter(validBetween.getToDate().atZone(zoneId).toInstant()));
    }
}
