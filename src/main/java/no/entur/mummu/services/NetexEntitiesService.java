package no.entur.mummu.services;

import no.entur.mummu.resources.NotFoundException;
import no.entur.mummu.util.NetexIdFilter;
import no.entur.mummu.util.NetexTechnicalIdComparator;
import no.entur.mummu.util.StopPlaceTypesFilter;
import no.entur.mummu.util.TopographicPlacesFilter;
import no.entur.mummu.util.TransportModesFilter;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.StopTypeEnumeration;
import org.rutebanken.netex.model.VehicleModeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NetexEntitiesService {
    private final NetexEntitiesIndex netexEntitiesIndex;

    @Autowired
    public NetexEntitiesService(
            NetexEntitiesIndex netexEntitiesIndex
    ) {
        this.netexEntitiesIndex = netexEntitiesIndex;
    }

    public List<StopPlace> getStopPlaces(
            Integer count,
            Integer skip,
            List<String> ids,
            List<VehicleModeEnumeration> transportModes,
            List<StopTypeEnumeration> stopPlaceTypes,
            List<String> topographicPlaceIds
    ) {
        return netexEntitiesIndex.getStopPlaceIndex().getLatestVersions().stream()
                .filter(new NetexIdFilter(ids))
                .filter(new TransportModesFilter(transportModes))
                .filter(new StopPlaceTypesFilter(stopPlaceTypes))
                .filter(new TopographicPlacesFilter(topographicPlaceIds, netexEntitiesIndex.getTopographicPlaceIndex()))
                .sorted(new NetexTechnicalIdComparator())
                .skip(skip)
                .limit(ids == null ? count : ids.size())
                .collect(Collectors.toList());
    }

    public StopPlace getStopPlace(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }

    public Collection<StopPlace> getStopPlaceVersions(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getAllVersions(id)
        ).orElseThrow(NotFoundException::new);
    }

    public StopPlace getStopPlaceVersion(String id, String version) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getVersion(id, version)
        ).orElseThrow(NotFoundException::new);
    }
}
