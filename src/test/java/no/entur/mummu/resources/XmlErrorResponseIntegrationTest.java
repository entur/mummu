package no.entur.mummu.resources;

import no.entur.mummu.MummuApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Error responses have to be marshallable as XML, not just as JSON.
 * <p>
 * {@link no.entur.mummu.config.NetexHttpMessageConverter} answers
 * {@code supports()} for every class, so it — not Jackson — is the converter
 * Spring picks to write an {@link ErrorResponse} under
 * {@code Accept: application/xml}. Its marshaller therefore has to come from a
 * JAXBContext that knows {@code ErrorResponse}; when it did not, marshalling
 * threw mid-write and the container turned a 404 into a 500.
 * <p>
 * This runs against a real servlet container on purpose. MockMvc does not
 * dispatch through the container's error handling, so it reports the failure as
 * a 404 with an empty body and would let the bug straight back in.
 */
@ActiveProfiles("test")
@SpringBootTest(classes = MummuApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class XmlErrorResponseIntegrationTest {

    private static final String UNKNOWN_STOP_PLACE = "/stop-places/NSR:StopPlace:123456789";

    @LocalServerPort
    private int port;

    private HttpResponse<String> get(String path, String accept) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("Accept", accept)
                .GET()
                .build();
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    void unknownIdIsNotFoundAsXml() throws Exception {
        HttpResponse<String> response = get(UNKNOWN_STOP_PLACE, "application/xml");

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("RESOURCE_NOT_FOUND"),
                "the error body was not marshalled as XML: " + response.body());
    }

    @Test
    void unknownIdIsNotFoundAsJson() throws Exception {
        HttpResponse<String> response = get(UNKNOWN_STOP_PLACE, "application/json");

        assertEquals(404, response.statusCode());
        assertTrue(response.body().contains("RESOURCE_NOT_FOUND"),
                "the error body was not marshalled as JSON: " + response.body());
    }

    /**
     * The 400 path carries the {@code details} map, which is the part of
     * ErrorResponse most likely to defeat JAXB.
     */
    @Test
    void invalidParameterIsBadRequestAsXml() throws Exception {
        HttpResponse<String> response = get("/stop-places?count=notanumber", "application/xml");

        assertEquals(400, response.statusCode());
        assertTrue(response.body().contains("INVALID_PARAMETER"),
                "the error body was not marshalled as XML: " + response.body());
        assertTrue(response.body().contains("providedValue"),
                "the details map was dropped: " + response.body());
    }

    @Test
    void stillServesNetexXmlSuccessfully() throws Exception {
        HttpResponse<String> response = get("/stop-places?count=1", "application/xml");

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("StopPlace"),
                "NeTEx marshalling regressed: " + response.body());
    }
}
