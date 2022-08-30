package no.entur.mummu.util;

import org.entur.netex.index.api.NetexEntityIndex;
import org.entur.netex.index.api.VersionedNetexEntityIndex;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.VersionOfObjectRefStructure;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class TopographicPlacesFilter implements Predicate<StopPlace> {
    private final List<String> topographicPlaceIds;
    private final VersionedNetexEntityIndex<TopographicPlace> entityIndex;

    public TopographicPlacesFilter(List<String> topographicPlaceIds, VersionedNetexEntityIndex<TopographicPlace> entityIndex) {
        this.topographicPlaceIds = topographicPlaceIds;
        this.entityIndex = entityIndex;
    }

    @Override
    public boolean test(StopPlace stopPlace) {
        if (topographicPlaceIds == null) { return true; }

        if (stopPlace.getTopographicPlaceRef() == null) { return false; }

        return topographicPlaceIds.stream()
                    .anyMatch(id ->
                        this.matchTopographicPlaceId(id, entityIndex.getLatestVersion(stopPlace.getTopographicPlaceRef().getRef())));
    }

    private boolean matchTopographicPlaceId(String id, TopographicPlace topographicPlace) {
        if (id.equalsIgnoreCase(topographicPlace.getId())) {
            return true;
        }

        return Optional.ofNullable(
                topographicPlace.getParentTopographicPlaceRef()
        ).stream().map(VersionOfObjectRefStructure::getRef)
                .anyMatch(ref -> ref.equalsIgnoreCase(id));
    }
}
