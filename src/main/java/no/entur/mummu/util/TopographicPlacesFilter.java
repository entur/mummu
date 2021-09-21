package no.entur.mummu.util;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.entur.netex.index.api.NetexEntityIndex;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TopographicPlace;

import java.util.List;
import java.util.function.Predicate;

public class TopographicPlacesFilter implements Predicate<StopPlace> {
    private final List<String> topographicPlaceIds;
    private final NetexEntityIndex<TopographicPlace> entityIndex;

    public TopographicPlacesFilter(List<String> topographicPlaceIds, NetexEntityIndex<TopographicPlace> entityIndex) {
        this.topographicPlaceIds = topographicPlaceIds;
        this.entityIndex = entityIndex;
    }

    @Override
    public boolean test(StopPlace stopPlace) {
        if (topographicPlaceIds == null) { return true; }

        if (stopPlace.getTopographicPlaceRef() == null) { return false; }

        boolean match = topographicPlaceIds.stream().anyMatch(id -> stopPlace.getTopographicPlaceRef().getRef().equalsIgnoreCase(id));

        if (!match) {
            match = topographicPlaceIds.stream()
                    .anyMatch(id -> {
                        var topoPlace = entityIndex.get(stopPlace.getTopographicPlaceRef().getRef());
                        if (topoPlace == null || topoPlace.getParentTopographicPlaceRef() == null) {
                            return false;
                        } else {
                            return topoPlace.getParentTopographicPlaceRef().getRef().equalsIgnoreCase(id);
                        }
                    });
        }

        return match;
    }
}
