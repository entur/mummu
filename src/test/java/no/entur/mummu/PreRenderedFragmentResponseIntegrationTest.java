package no.entur.mummu;

import io.micrometer.core.instrument.MeterRegistry;
import no.entur.mummu.serializers.NetexJsonObjectMapper;
import no.entur.mummu.services.NetexEntitiesService;
import no.entur.mummu.services.StopPlacesRequestParams;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Guards the contract that makes pre-rendering safe: responses served from
 * cached fragments are byte-for-byte what Jackson produced before, and every
 * type the fragment converter does not claim is untouched.
 */
@ActiveProfiles("test")
@SpringBootTest(classes = MummuApplication.class)
@AutoConfigureMockMvc
class PreRenderedFragmentResponseIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private NetexEntitiesService netexEntitiesService;

    @Autowired
    private NetexJsonObjectMapper netexJsonObjectMapper;

    @Autowired
    private MeterRegistry meterRegistry;

    @Test
    void stopPlacesResponseIsUnchanged() throws Exception {
        byte[] expected = netexJsonObjectMapper.get()
                .writeValueAsBytes(netexEntitiesService.getStopPlaces(new StopPlacesRequestParams()));

        mvc.perform(get("/stop-places"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().bytes(expected));
    }

    @Test
    void quaysResponseIsUnchanged() throws Exception {
        byte[] expected = netexJsonObjectMapper.get()
                .writeValueAsBytes(netexEntitiesService.getQuays(10, 0, null));

        mvc.perform(get("/quays"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(expected));
    }

    @Test
    void singleStopPlaceResponseIsUnchanged() throws Exception {
        byte[] expected = netexJsonObjectMapper.get()
                .writeValueAsBytes(netexEntitiesService.getStopPlace("NSR:StopPlace:4004"));

        mvc.perform(get("/stop-places/NSR:StopPlace:4004"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(expected));
    }

    @Test
    void servesStopPlacesFromPreRenderedFragments() throws Exception {
        mvc.perform(get("/stop-places")).andExpect(status().isOk());

        // Asserted through the gauge operators actually watch, so the metric is
        // covered by the same test that proves the converter is wired in.
        var retained = meterRegistry.find("mummu.fragment.cache.bytes").gauge();
        assertNotNull(retained, "the fragment cache publishes no bytes gauge");
        assertTrue(retained.value() > 0,
                "no fragments were rendered, so the response was serialized by Jackson instead");
    }

    @Test
    void topographicPlacesResponseIsUnchanged() throws Exception {
        byte[] expected = netexJsonObjectMapper.get()
                .writeValueAsBytes(netexEntitiesService.getTopographicPlaces(10, 0, null));

        mvc.perform(get("/topographic-places"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(expected));
    }

    @Test
    void singleTopographicPlaceResponseIsUnchanged() throws Exception {
        byte[] expected = netexJsonObjectMapper.get()
                .writeValueAsBytes(netexEntitiesService.getTopographicPlaceById("KVE:TopographicPlace:0301"));

        mvc.perform(get("/topographic-places/KVE:TopographicPlace:0301"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(expected));
    }

    @Test
    void stillServesStopPlacesAsXml() throws Exception {
        mvc.perform(get("/stop-places").accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML));
    }

    @Test
    void stillServesTypesThatAreNotPreRendered() throws Exception {
        byte[] expected = netexJsonObjectMapper.get()
                .writeValueAsBytes(netexEntitiesService.getTariffZones(10, 0, null, null));

        mvc.perform(get("/tariff-zones"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(expected));
    }
}
