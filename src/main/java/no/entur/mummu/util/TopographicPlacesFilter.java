package no.entur.mummu.util;

import org.entur.netex.index.api.NetexEntityIndex;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.TopographicPlace_VersionStructure;

import java.util.List;
import java.util.Optional;
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

        return topographicPlaceIds.stream()
                    .anyMatch(id -> this.matchTopographicPlaceId(id, stopPlace, entityIndex));
    }

    private boolean matchTopographicPlaceId(String id, StopPlace stopPlace, NetexEntityIndex<TopographicPlace> entityIndex) {
        if (stopPlace.getTopographicPlaceRef().getRef().equalsIgnoreCase(id)) {
            return true;
        }

        return Optional.ofNullable(
                entityIndex.get(stopPlace.getTopographicPlaceRef().getRef())
        ).stream().map(TopographicPlace_VersionStructure::getParentTopographicPlaceRef)
                .anyMatch(topographicPlaceRefStructure -> topographicPlaceRefStructure.getRef().equalsIgnoreCase(id));
    }
}
