package no.entur.mummu.updater;

import no.entur.mummu.repositories.StopPlaceRepository;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.irkalla.avro.EnumType;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.rutebanken.netex.model.Quay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StopPlacesUpdater {
    private static final Logger log = LoggerFactory.getLogger(StopPlacesUpdater.class);
    private final NetexEntitiesIndex netexEntitiesIndex;
    private final StopPlaceRepository repository;

    @Autowired
    public StopPlacesUpdater(NetexEntitiesIndex netexEntitiesIndex, StopPlaceRepository repository) {
        this.netexEntitiesIndex = netexEntitiesIndex;
        this.repository = repository;
    }

    public void receiveStopPlaceUpdate(StopPlaceChangelogEvent event) {
        String stopPlaceId = event.getStopPlaceId().toString();

        if (event.getEventType().equals(EnumType.DELETE)) {
            log.debug("deleting stopPlace id={}", stopPlaceId);
            netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(stopPlaceId)
                    .getQuays().getQuayRefOrQuay().forEach(quay -> netexEntitiesIndex.getQuayIndex().remove(((Quay) quay).getId()));
            netexEntitiesIndex.getStopPlaceIndex().remove(stopPlaceId);
        } else {
            log.debug("updating stopPlace id={}", stopPlaceId);
            var stopPlaceUpdate = repository.getStopPlaceUpdate(stopPlaceId);
            netexEntitiesIndex.getStopPlaceIndex().put(stopPlaceId, stopPlaceUpdate.getVersions());
            stopPlaceUpdate.getQuayVersions().forEach((s, quays) -> netexEntitiesIndex.getQuayIndex().put(s, quays));
            stopPlaceUpdate.getParkingVersions().forEach((s, parkings) -> netexEntitiesIndex.getParkingIndex().put(s, parkings));
        }
    }
}
