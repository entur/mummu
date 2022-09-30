package no.entur.mummu.repositories;

import org.entur.netex.NetexParser;
import org.rutebanken.netex.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collection;
import java.util.Objects;


@Component
public class StopPlaceRepository {

    private final RestTemplate tiamatClient;

    @Autowired
    public StopPlaceRepository(RestTemplate tiamatClient) {
        this.tiamatClient = tiamatClient;
    }

    public Collection<StopPlace> getStopPlaceVersions(String stopPlaceId) {
        try {
            var response = tiamatClient.exchange(
                    "/netex?idList=" + stopPlaceId + "&topographicPlaceExportMode=NONE&tariffZoneExportMode=NONE&versionValidity=ALL",
                    HttpMethod.GET,
                    null,
                    Resource.class
            );

            NetexParser parser = new NetexParser();
            var index = parser.parse(Objects.requireNonNull(response.getBody()).getInputStream());
            return index.getStopPlaceIndex().getAllVersions(stopPlaceId);
        } catch (IOException | NullPointerException exception) {
            // log warning?

            return null;
        }
    }
}
