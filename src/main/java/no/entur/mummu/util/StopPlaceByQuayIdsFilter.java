package no.entur.mummu.util;

import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class StopPlaceByQuayIdsFilter implements Predicate<StopPlace> {

    private final List<String> quayIds;

    public StopPlaceByQuayIdsFilter(List<String> quayIds) {
        this.quayIds = quayIds;
    }

    @Override
    public boolean test(StopPlace stopPlace) {
        if (quayIds == null || quayIds.isEmpty()) {
            return true;
        }

        if (stopPlace.getQuays() == null) {
            return false;
        }

        return stopPlace.getQuays().getQuayRefOrQuay().stream()
                .filter(Objects::nonNull)
                .anyMatch(v -> quayIds.contains(((Quay) v).getId()));
    }
}
