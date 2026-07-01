package no.entur.mummu.web;

import no.entur.mummu.MummuApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = MummuApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "no.entur.mummu.list.enforce-max-count=true",
        "no.entur.mummu.list.max-count=5",
        "no.entur.mummu.list.max-count-overrides.quays=9999"
})
class MaxCountInterceptorIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void rejectsCountOverDefaultMax() throws Exception {
        mvc.perform(get("/stop-places?count=10").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void allowsCountWithinMax() throws Exception {
        mvc.perform(get("/stop-places?count=3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void allowsCountUpToEndpointOverride() throws Exception {
        mvc.perform(get("/quays?count=10").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
