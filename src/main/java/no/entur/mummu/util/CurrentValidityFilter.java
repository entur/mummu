package no.entur.mummu.util;

import org.rutebanken.netex.model.EntityInVersionStructure;

import java.time.Instant;
import java.time.ZoneId;
import java.util.function.Predicate;

public class CurrentValidityFilter implements Predicate<EntityInVersionStructure> {
    private final ZoneId zoneId;
    private final Boolean  includeDeactivated;

    public CurrentValidityFilter(ZoneId zoneId) {
        this(zoneId, false);
    }

    public CurrentValidityFilter(ZoneId zoneId, Boolean includeDeactivated) {
        this.zoneId = zoneId;
        this.includeDeactivated = includeDeactivated;
    }

    @Override
    public boolean test(EntityInVersionStructure entity) {
        return includeDeactivated || entity.getValidBetween().stream()
                .filter(validBetween -> validBetween.getToDate() != null)
                .noneMatch(validBetween -> Instant.now().isAfter(validBetween.getToDate().atZone(zoneId).toInstant()));
    }
}
