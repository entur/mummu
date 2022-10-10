package no.entur.mummu;

import no.entur.mummu.repositories.StopPlaceRepository;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

class StopPlaceRepositoryTest {

    public static void main(final String[] args) throws IOException {
        RestTemplate client = new RestTemplateBuilder()
                .build();

        StopPlaceRepository repository = new StopPlaceRepository(client, "https://api.dev.entur.io/stop-places/v1");
        var update = repository.getStopPlaceUpdate("NSR:StopPlace:337");
        Assertions.assertEquals(43, update.getVersions().get("NSR:StopPlace:337").size());
    }
}
