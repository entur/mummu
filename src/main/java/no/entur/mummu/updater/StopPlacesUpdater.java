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

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
            log.info("deleting stopPlace id={}", stopPlaceId);
            netexEntitiesIndex.getStopPlaceIndex().getLatestVersion(stopPlaceId)
                    .getQuays().getQuayRefOrQuay().forEach(quay -> netexEntitiesIndex.getQuayIndex().remove(((Quay) quay).getId()));
            netexEntitiesIndex.getStopPlaceIndex().remove(stopPlaceId);
        } else {
            log.info("updating stopPlace id={}", stopPlaceId);
            var stopPlaceUpdate = repository.getStopPlaceUpdate(stopPlaceId);

            if (stopPlaceUpdate != null) {
                stopPlaceUpdate.getVersions().forEach((s, versions) -> netexEntitiesIndex.getStopPlaceIndex().put(s, versions));
                stopPlaceUpdate.getQuayVersions().forEach((s, quays) -> netexEntitiesIndex.getQuayIndex().put(s, quays));
                stopPlaceUpdate.getParkingVersions().forEach((s, parkings) -> netexEntitiesIndex.getParkingIndex().put(s, parkings));
            }
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
}
