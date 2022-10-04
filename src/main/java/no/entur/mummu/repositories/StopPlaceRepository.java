package no.entur.mummu.repositories;

import no.entur.mummu.updater.StopPlaceUpdate;
import org.entur.netex.NetexParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;


@Component
public class StopPlaceRepository {
    private static final Logger logger = LoggerFactory.getLogger(StopPlaceRepository.class);
    private final RestTemplate tiamatClient;

    private final String stopPlaceUrl = UriComponentsBuilder.fromPath("/netex")
            .queryParam("idList", "{idList}")
            .queryParam("topographicPlaceExportMode", "{topographicPlaceExportMode}")
            .queryParam("tariffZoneExportMode", "{tariffZoneExportMode}")
            .queryParam("fareZoneExportMode", "{fareZoneExportMode}")
            .queryParam("allVersions", "{allVersions}")
            .queryParam("size", "{size}")
            .encode()
            .toUriString();

    @Autowired
    public StopPlaceRepository(RestTemplate tiamatClient) {
        this.tiamatClient = tiamatClient;
    }

    public StopPlaceUpdate getStopPlaceUpdate(String stopPlaceId) {
        try {
            var response = tiamatClient.exchange(
                    stopPlaceUrl,
                    HttpMethod.GET,
                    null,
                    Resource.class,
                    Map.of(
                            "idList", stopPlaceId,
                            "topographicPlaceExportMode", "NONE",
                            "tariffZoneExportMode", "NONE",
                            "fareZoneExportMode", "NONE",
                            "allVersions", true,
                            "size", Integer.MAX_VALUE
                    )
            );

            NetexParser parser = new NetexParser();
            var index = parser.parse(Objects.requireNonNull(response.getBody()).getInputStream());

            StopPlaceUpdate stopPlaceUpdate = new StopPlaceUpdate();
            stopPlaceUpdate.setStopPlaceId(stopPlaceId);
            stopPlaceUpdate.setVersions(index.getStopPlaceIndex().getAllVersions(stopPlaceId));
            stopPlaceUpdate.setQuayVersions(index.getQuayIndex().getAllVersions());
            stopPlaceUpdate.setParkingVersions(index.getParkingIndex().getAllVersions());

            return stopPlaceUpdate;
        } catch (IOException | NullPointerException exception) {
            logger.warn("Failed to get update from stop place repository. Skipping...");
            return null;
        }
    }
}
