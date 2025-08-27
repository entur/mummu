package no.entur.mummu.updater;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import no.entur.mummu.services.NetexEntitiesIndexLoader;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.helper.stopplace.changelog.StopPlaceChangelog;
import org.rutebanken.helper.stopplace.changelog.StopPlaceChangelogListener;
import org.rutebanken.netex.model.Quay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@ConditionalOnProperty(
        name = "no.entur.mummu.stopplacesupdater.enabled",
        havingValue = "true"
)
public class StopPlacesUpdater implements StopPlaceChangelogListener {
    private static final Logger log = LoggerFactory.getLogger(StopPlacesUpdater.class);
    private final NetexEntitiesIndexLoader netexEntitiesIndexLoader;
    private final NetexEntitiesIndex netexEntitiesIndex;
    private final StopPlaceChangelog stopPlaceChangelog;

    @Autowired
    public StopPlacesUpdater(NetexEntitiesIndexLoader netexEntitiesIndexLoader, StopPlaceChangelog stopPlaceChangelog) {
        this.netexEntitiesIndexLoader = netexEntitiesIndexLoader;
        this.netexEntitiesIndex = netexEntitiesIndexLoader.getNetexEntitiesIndex();
        this.stopPlaceChangelog = stopPlaceChangelog;
    }

    @PostConstruct
    public void init() {
        stopPlaceChangelog.registerStopPlaceChangelogListener(this);
    }

    @PreDestroy
    public void preDestroy() {
        stopPlaceChangelog.unregisterStopPlaceChangelogListener(this);
    }

    @Override
    public void onStopPlaceCreated(String id, InputStream stopPlace) {
        log.info("Creating stop place with id {}", id);
        update(id, stopPlace);
    }

    @Override
    public void onStopPlaceUpdated(String id, InputStream stopPlace) {
        log.info("Updating stop place with id {}", id);
        update(id, stopPlace);
    }

    @Override
    public void onStopPlaceDeactivated(String id, InputStream stopPlace) {
        log.info("Deactivating stop place with id {}", id);
        update(id, stopPlace);
    }

    @Override
    public void onStopPlaceDeleted(String id) {
        log.info("Deleting stop place with id {}", id);
        delete(id);
    }

    private void delete(String stopPlaceId) {
        log.info("deleting stopPlace id={}", stopPlaceId);
        var stopPlace = netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(stopPlaceId);
        if (stopPlace == null) {
            log.info("couldn't find stop place in index {}", stopPlaceId);
        } else {
            if (stopPlace.getQuays() != null) {
                stopPlace.getQuays().getQuayRefOrQuay().forEach(quay -> netexEntitiesIndex.getQuayIndex().remove(((Quay) quay).getId()));
            }
            netexEntitiesIndex.getStopPlaceIndex().getLatestVersions().forEach(stop -> {
                if (stop.getParentSiteRef() != null && stop.getParentSiteRef().getRef().equals(stopPlaceId)) {
                    netexEntitiesIndex.getStopPlaceIndex().remove(stop.getId());
                }
            });
            netexEntitiesIndex.getStopPlaceIndex().remove(stopPlaceId);
        }
    }

    private void update(String stopPlaceId, InputStream stopPlaceUpdate) {
        try {
            netexEntitiesIndexLoader.updateWithPublicationDeliveryStream(stopPlaceUpdate);
        } catch (RuntimeException exception) {
            log.warn("Failed to parse response for id {} from stop place repository. Skipping due to {}", stopPlaceId, exception.toString());
        }
    }
}
