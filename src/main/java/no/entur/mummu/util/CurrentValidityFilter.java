package no.entur.mummu.util;

import org.rutebanken.netex.model.StopPlace;

import java.time.Instant;
import java.time.ZoneId;
import java.util.function.Predicate;

public class CurrentValidityFilter implements Predicate<StopPlace> {
    private final ZoneId zoneId;
    public CurrentValidityFilter(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    @Override
    public boolean test(StopPlace stopPlace) {
        return stopPlace.getValidBetween().stream()
                .filter(validBetween -> validBetween.getToDate() != null)
                .noneMatch(validBetween -> Instant.now().isAfter(validBetween.getToDate().atZone(zoneId).toInstant()));
    }
}
