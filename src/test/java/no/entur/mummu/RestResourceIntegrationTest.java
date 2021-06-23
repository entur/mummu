package no.entur.mummu;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest(classes = MummuApplication.class)
@AutoConfigureMockMvc
class RestResourceIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void testGetGroupOfStopPlacesById() throws Exception {
        mvc.perform(get("/groups-of-stop-places/NSR:GroupOfStopPlaces:1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Oslo"));
    }

    @Test
    void testGetStopPlaceById() throws Exception {
        mvc.perform(get("/stop-places/NSR:StopPlace:4004")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                    .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Jernbanetorget"));
    }

    @Test
    void testGetParkingsByStopPlaceId() throws Exception {
        mvc.perform(get("/stop-places/NSR:StopPlace:11/parkings")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value("NSR:Parking:1"));
    }

    @Test
    void testGetQuayById() throws Exception {
        mvc.perform(get("/quays/NSR:Quay:7209")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.privateCode.value").value("11"));
    }

    @Test
    void testGetStopPlaceByQuayId() throws Exception {
        mvc.perform(get("/quays/NSR:Quay:7209/stop-place")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("NSR:StopPlace:4004"));
    }

    @Test
    void testGetParkingById() throws Exception {
        mvc.perform(get("/parkings/NSR:Parking:1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Drammen"));
    }

    @Test
    void testGetTopographicPlaceById() throws Exception {
        mvc.perform(get("/topographic-places/KVE:TopographicPlace:0301")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.countryRef.ref").value("NO"));
    }

    @Test
    void testGetTariffZoneById() throws Exception {
        mvc.perform(get("/tariff-zones/ATB:TariffZone:13")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("E2"));
    }

    @Test
    void testGetFareZoneById() throws Exception {
        mvc.perform(get("/fare-zones/BRA:FareZone:22")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Drammen"));
    }

    @Test
    void testGetUnknownStopPlaceReturnsNotFound() throws Exception {
        mvc.perform(get("/stop-places/FOO:StopPlace:1234")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
