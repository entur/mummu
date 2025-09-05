package no.entur.mummu.updater;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import no.entur.mummu.services.NetexEntitiesIndexLoader;
import org.rutebanken.helper.stopplace.changelog.StopPlaceChangelog;
import org.rutebanken.helper.stopplace.changelog.StopPlaceChangelogListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@ConditionalOnProperty(
        name = "no.entur.mummu.stopplacesupdater.enabled",
        havingValue = "true"
)
public class StopPlacesUpdater implements StopPlaceChangelogListener {
    private static final Logger log = LoggerFactory.getLogger(StopPlacesUpdater.class);
    private final NetexEntitiesIndexLoader netexEntitiesIndexLoader;
    private final StopPlaceChangelog stopPlaceChangelog;

    @Autowired
    public StopPlacesUpdater(NetexEntitiesIndexLoader netexEntitiesIndexLoader, StopPlaceChangelog stopPlaceChangelog) {
        this.netexEntitiesIndexLoader = netexEntitiesIndexLoader;
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

    private void update(String stopPlaceId, InputStream stopPlaceUpdate) {
        try (stopPlaceUpdate) {
            netexEntitiesIndexLoader.updateWithPublicationDeliveryStream(stopPlaceUpdate);
        } catch (RuntimeException | IOException exception) {
            log.warn("Failed to stop with id {} from stop place repository. Skipping due to {}", stopPlaceId, exception.toString());
        }
    }
}
