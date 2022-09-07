package no.entur.mummu.services;

import no.entur.mummu.resources.NotFoundException;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.netex.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NetexEntitiesService {
    private final NetexEntitiesIndex netexEntitiesIndex;

    @Autowired
    public NetexEntitiesService(
            NetexEntitiesIndex netexEntitiesIndex
    ) {
        this.netexEntitiesIndex = netexEntitiesIndex;
    }

    public StopPlace getStopPlace(String id) {
        return Optional.ofNullable(
                netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(id)
        ).orElseThrow(NotFoundException::new);
    }
}
