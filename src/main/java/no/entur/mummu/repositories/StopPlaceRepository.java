package no.entur.mummu.repositories;

import no.entur.mummu.updater.StopPlaceUpdate;
import org.entur.netex.NetexParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;


@Component
public class StopPlaceRepository {
    private static final Logger logger = LoggerFactory.getLogger(StopPlaceRepository.class);
    private final RestTemplate tiamatClient;
    private final String tiamatUrl;

    @Autowired
    public StopPlaceRepository(RestTemplate tiamatClient, @Value("${no.entur.mummu.tiamat.url:https://api.dev.entur.io/stop-places/v1}") String tiamatUrl) {
        this.tiamatClient = tiamatClient;
        this.tiamatUrl = tiamatUrl;
    }

    public StopPlaceUpdate getStopPlaceUpdate(String stopPlaceId) {
        String stopPlaceUrl = UriComponentsBuilder.fromHttpUrl(tiamatUrl + "/netex")
                .queryParam("idList", "{idList}")
                .queryParam("topographicPlaceExportMode", "{topographicPlaceExportMode}")
                .queryParam("tariffZoneExportMode", "{tariffZoneExportMode}")
                .queryParam("fareZoneExportMode", "{fareZoneExportMode}")
                .queryParam("allVersions", "{allVersions}")
                .queryParam("size", "{size}")
                .encode()
                .toUriString();

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
            stopPlaceUpdate.setVersions(index.getStopPlaceIndex().getAllVersions());
            stopPlaceUpdate.setQuayVersions(index.getQuayIndex().getAllVersions());
            stopPlaceUpdate.setParkingVersions(index.getParkingIndex().getAllVersions());

            return stopPlaceUpdate;
        } catch (RestClientException | IOException exception) {
            logger.warn("Failed to get update for id {} from stop place repository. Trying again due to {}", stopPlaceId, exception.toString());
            throw new RuntimeException(exception);
        } catch (RuntimeException exception) {
            logger.warn("Failed to parse response for id {} from stop place repository. Skipping due to {}", stopPlaceId, exception.toString());
            return null;
        }
    }
}
