package no.entur.mummu.services;

import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.irkalla.avro.EnumType;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StopPlacesUpdater {
    private static final Logger log = LoggerFactory.getLogger(StopPlacesUpdater.class);
    private final NetexEntitiesIndex netexEntitiesIndex;

    @Autowired
    public StopPlacesUpdater(NetexEntitiesIndex netexEntitiesIndex) {
        this.netexEntitiesIndex = netexEntitiesIndex;
    }

    public void receiveStopPlaceUpdate(StopPlaceChangelogEvent event) {

        log.info("event={}", event);

        String stopPlaceId = event.getStopPlaceId().toString();

        if (event.getEventType().equals(EnumType.DELETE)) {
            // - remove entry from index directly
        } else {
            // All other event types means replacing the entry in the index:
            // - get all versions of stop place from tiamat
            // - https://api.dev.entur.io/stop-places/v1/netex?idList=NSR:StopPlace:2531&topographicPlaceExportMode=NONE&tariffZoneExportMode=NONE&versionValidity=ALL
            // - then replace entry in index
        }
    }
}
