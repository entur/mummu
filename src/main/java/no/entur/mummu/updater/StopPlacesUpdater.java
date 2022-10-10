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

import java.time.ZoneId;

@Service
public class StopPlacesUpdater {
    private static final Logger log = LoggerFactory.getLogger(StopPlacesUpdater.class);
    private final NetexEntitiesIndex netexEntitiesIndex;
    private final StopPlaceRepository repository;
    private static final String DEFAULT_TIME_ZONE = "Europe/Oslo";

    @Autowired
    public StopPlacesUpdater(NetexEntitiesIndex netexEntitiesIndex, StopPlaceRepository repository) {
        this.netexEntitiesIndex = netexEntitiesIndex;
        this.repository = repository;
    }

    public void receiveStopPlaceUpdate(StopPlaceChangelogEvent event) {
        if (filter(event)) {
            log.info("Discarded event {}", event);
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
        var localPublicationTimestamp = netexEntitiesIndex.getPublicationTimestamp();
        var timeZone = netexEntitiesIndex.getSiteFrames().stream()
                .findFirst()
                .map(frame -> frame.getFrameDefaults().getDefaultLocale().getTimeZone())
                .orElse(DEFAULT_TIME_ZONE);
        var publicationTime = localPublicationTimestamp.atZone(ZoneId.of(timeZone)).toInstant();
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

        if (stopPlaceUpdate != null) {
            stopPlaceUpdate.getVersions().forEach((s, versions) -> netexEntitiesIndex.getStopPlaceIndex().put(s, versions));
            stopPlaceUpdate.getQuayVersions().forEach((s, quays) -> netexEntitiesIndex.getQuayIndex().put(s, quays));
            stopPlaceUpdate.getParkingVersions().forEach((s, parkings) -> netexEntitiesIndex.getParkingIndex().put(s, parkings));
        }
    }
}
