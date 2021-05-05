package org.entur.mummu;

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
public class RestResourceIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Test
    public void testGetGroupOfStopPlacesById() throws Exception {
        mvc.perform(get("/group-of-stop-places/NSR:GroupOfStopPlaces:1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Oslo"));
    }

    @Test
    public void testGetStopPlaceById() throws Exception {
        mvc.perform(get("/stop-places/NSR:StopPlace:4004")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                    .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Jernbanetorget"));
    }

    @Test
    public void testGetQuayById() throws Exception {
        mvc.perform(get("/quays/NSR:Quay:7209")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.privateCode.value").value("11"));
    }

    @Test
    public void testGetParkingById() throws Exception {
        mvc.perform(get("/parkings/NSR:Parking:1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Drammen"));
    }

    @Test
    public void testGetTopographicPlaceById() throws Exception {
        mvc.perform(get("/topographic-places/KVE:TopographicPlace:0301")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.countryRef.ref").value("NO"));
    }

    @Test
    public void testGetTariffZoneById() throws Exception {
        mvc.perform(get("/tariff-zones/ATB:TariffZone:13")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("E2"));
    }

    @Test
    public void testGetFareZoneById() throws Exception {
        mvc.perform(get("/fare-zones/BRA:FareZone:22")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Drammen"));
    }
}
