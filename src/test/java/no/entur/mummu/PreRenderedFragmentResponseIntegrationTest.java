package no.entur.mummu;

import no.entur.mummu.serializers.NetexJsonFragmentCache;
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
    private NetexJsonFragmentCache fragmentCache;

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

        assertTrue(fragmentCache.size() > 0,
                "no fragments were rendered, so the response was serialized by Jackson instead");
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
