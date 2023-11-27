package no.entur.mummu;

import jakarta.xml.bind.JAXBElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.StopPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;

import static org.hamcrest.core.StringStartsWith.startsWith;
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
    void testGetGroupsOfStopPlaces() throws Exception {

        mvc.perform(get("/groups-of-stop-places"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    void testGetGroupOfStopPlacesById() throws Exception {
        mvc.perform(get("/groups-of-stop-places/NSR:GroupOfStopPlaces:1"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Oslo"));
    }

    @Test
    void testGetStopPlaces() throws Exception {
        mvc.perform(get("/stop-places"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    void testGetFilterStopPlaces() throws Exception {
        mvc.perform(get("/stop-places?stopPlaceTypes=ONSTREET_TRAM&topographicPlaceIds=KVE:TopographicPlace:03"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].topographicPlaceRef.ref").value(startsWith("KVE:TopographicPlace:03")));
    }

    @Test
    void testGetStopPlaceById() throws Exception {
        mvc.perform(get("/stop-places/NSR:StopPlace:4004"))
                .andExpect(status().isOk())
                .andExpect(content()
                    .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Jernbanetorget"));
    }

    @Test
    void testGetStopPlaceByIdAllVersions() throws Exception {
        mvc.perform(get("/stop-places/NSR:StopPlace:4004/versions"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    void testGetStopPlaceByIdAndVersion() throws Exception {
        mvc.perform(get("/stop-places/NSR:StopPlace:4004/versions/71"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.version").value("71"));
    }

    @Test
    void testGetParkingsByStopPlaceId() throws Exception {
        mvc.perform(get("/stop-places/NSR:StopPlace:5543/parkings"))
                .andExpect(status().isOk())
                .andExpect(content()
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[0].id").value("NSR:Parking:1"));
    }

    @Test
    void testGetParkingsWithUnknownStopPlaceIdGivesNotFound() throws Exception {
        mvc.perform(get("/stop-places/NSR:StopPlace:2/parkings")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetParkingsWithStopPlaceWithoutParkingsGivesEmptyList() throws Exception {
        mvc.perform(get("/stop-places/NSR:StopPlace:4004/parkings"))
                .andExpect(status().isOk())
                .andExpect(content()
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isEmpty());
    }

    @Test
    void testGetQuays() throws Exception {
        mvc.perform(get("/quays"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    void testGetQuayById() throws Exception {
        mvc.perform(get("/quays/NSR:Quay:7209"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.privateCode.value").value("11"));
    }

    @Test
    void testGetStopPlaceByQuayId() throws Exception {
        mvc.perform(get("/quays/NSR:Quay:7209/stop-place"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("NSR:StopPlace:4004"));
    }

    @Test
    void testGetQuayByIdAllVersions() throws Exception {
        mvc.perform(get("/quays/NSR:Quay:7209/versions"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    void testGetQuayByIdAndVersion() throws Exception {
        mvc.perform(get("/quays/NSR:Quay:7209/versions/71"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.version").value("71"));
    }

    @Test
    void testGetParkings() throws Exception {
        mvc.perform(get("/parkings"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    void testGetParkingById() throws Exception {
        mvc.perform(get("/parkings/NSR:Parking:1"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Drammen"));
    }

    @Test
    void testGetTopographicPlaces() throws Exception {
        mvc.perform(get("/topographic-places"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    void testGetTopographicPlaceById() throws Exception {
        mvc.perform(get("/topographic-places/KVE:TopographicPlace:0301"))
                .andExpect(status().isOk())
                .andExpect(content()
                .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.countryRef.ref").value("NO"));
    }

    @Test
    void getTariffZonesByIds() throws Exception {
        mvc.perform(get("/tariff-zones?ids=ATB:TariffZone:13"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    void testGetTariffZoneById() throws Exception {
        mvc.perform(get("/tariff-zones/ATB:TariffZone:13"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("E2"));
    }

    @Test
    void testGetGroupsOfTariffZones() throws Exception {
        mvc.perform(get("/groups-of-tariff-zones"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    void testGetGroupOfTariffZoneById() throws Exception {
        mvc.perform(get("/groups-of-tariff-zones/NOR:GroupOfTariffZones:1"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.members.tariffZoneRef[0].ref").value("NOR:FareZone:77"));
    }

    @Test
    void testGetFareZones() throws Exception {
        mvc.perform(get("/fare-zones"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*]").isNotEmpty());
    }

    @Test
    void testGetFareZoneById() throws Exception {
        mvc.perform(get("/fare-zones/BRA:FareZone:22"))
                .andExpect(status().isOk())
                .andExpect(content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name.value").value("Drammen"));
    }

    @Test
    void testGetUnknownStopPlaceReturnsNotFound() throws Exception {
        mvc.perform(get("/stop-places/FOO:StopPlace:1234"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testXMLOutputCanBeMarshalled() throws Exception {
        ResultActions resultActions = mvc.perform(get("/stop-places/NSR:StopPlace:4004")
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk());
        MvcResult mvcResult = resultActions.andReturn();
        String contentAsString = mvcResult.getResponse().getContentAsString();
        Unmarshaller unmarshaller = JAXBContext
                .newInstance(StopPlace.class)
                .createUnmarshaller();
        JAXBElement<StopPlace> stopPlace = (JAXBElement<StopPlace>) unmarshaller.unmarshal(new ByteArrayInputStream(contentAsString.getBytes()));
        Assertions.assertEquals("NSR:StopPlace:4004", stopPlace.getValue().getId());
    }
}
