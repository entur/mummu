package no.entur.mummu.updater;

import no.entur.mummu.repositories.StopPlaceRepository;
import no.entur.mummu.services.NetexEntitiesIndexLoader;
import org.entur.netex.index.api.NetexEntitiesIndex;
import org.rutebanken.irkalla.avro.EnumType;
import org.rutebanken.irkalla.avro.StopPlaceChangelogEvent;
import org.rutebanken.netex.model.Quay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;

@Service
public class StopPlacesUpdater {
    private static final Logger log = LoggerFactory.getLogger(StopPlacesUpdater.class);
    private final NetexEntitiesIndexLoader netexEntitiesIndexLoader;
    private final NetexEntitiesIndex netexEntitiesIndex;
    private final Instant publicationTime;
    private final StopPlaceRepository repository;
    private static final String DEFAULT_TIME_ZONE = "Europe/Oslo";

    @Autowired
    public StopPlacesUpdater(NetexEntitiesIndexLoader netexEntitiesIndexLoader, StopPlaceRepository repository) {
        this.netexEntitiesIndexLoader = netexEntitiesIndexLoader;
        this.netexEntitiesIndex = netexEntitiesIndexLoader.getNetexEntitiesIndex();
        this.publicationTime = getPublicationTime(netexEntitiesIndex);
        this.repository = repository;
    }

    public void receiveStopPlaceUpdate(StopPlaceChangelogEvent event) {
        if (filter(event)) {
            log.info("Discarded event {}", event);
            return;
        }

        String stopPlaceId = event.getStopPlaceId().toString();

        if (event.getEventType().equals(EnumType.DELETE)) {
            delete(stopPlaceId);
        } else {
            update(event, stopPlaceId);
        }
    }

    protected boolean filter(StopPlaceChangelogEvent event) {
        if (event.getStopPlaceChanged() == null) {
            return true;
        }

        var changedTime = event.getStopPlaceChanged();
        return changedTime.isBefore(publicationTime);
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

    private void update(StopPlaceChangelogEvent event, String stopPlaceId) {
        log.info("event type {} stopPlace id={}", event.getEventType(), stopPlaceId);
        var stopPlaceUpdate = repository.getStopPlaceUpdate(stopPlaceId);
        try {
            netexEntitiesIndexLoader.updateWithPublicationDeliveryStream(stopPlaceUpdate);
        } catch (RuntimeException exception) {
            log.warn("Failed to parse response for id {} from stop place repository. Skipping due to {}", stopPlaceId, exception.toString());
        }
    }

    private Instant getPublicationTime(NetexEntitiesIndex netexEntitiesIndex) {
        var localPublicationTimestamp = netexEntitiesIndex.getPublicationTimestamp();
        var timeZone = netexEntitiesIndex.getSiteFrames().stream()
                .findFirst()
                .map(frame -> frame.getFrameDefaults().getDefaultLocale().getTimeZone())
                .orElse(DEFAULT_TIME_ZONE);
        return localPublicationTimestamp.atZone(ZoneId.of(timeZone)).toInstant();
    }
}
