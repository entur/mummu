package no.entur.mummu.config;

import no.entur.mummu.MummuApplication;
import no.entur.mummu.serializers.NetexJsonObjectMapper;
import no.entur.mummu.services.NetexEntitiesIndexLoader;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.netex.model.TariffZone;
import org.rutebanken.netex.model.TopographicPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(classes = MummuApplication.class)
class NetexJsonFragmentHttpMessageConverterTest {

    @Autowired
    private NetexJsonFragmentHttpMessageConverter converter;

    @Autowired
    private NetexJsonObjectMapper netexJsonObjectMapper;

    @Autowired
    private NetexEntitiesIndexLoader loader;

    private static Type listOf(Class<?> element) {
        return ResolvableType.forClassWithGenerics(List.class, element).getType();
    }

    private List<StopPlace> stopPlaces() {
        return new ArrayList<>(loader.getNetexEntitiesIndex().getStopPlaceIndex().getLatestVersions());
    }

    private List<Quay> quays() {
        return new ArrayList<>(loader.getNetexEntitiesIndex().getQuayIndex().getLatestVersions());
    }

    private List<TopographicPlace> topographicPlaces() {
        return new ArrayList<>(loader.getNetexEntitiesIndex().getTopographicPlaceIndex().getLatestVersions());
    }

    private List<TariffZone> tariffZones() {
        return new ArrayList<>(loader.getNetexEntitiesIndex().getTariffZoneIndex().getLatestVersions());
    }

    private List<FareZone> fareZones() {
        return new ArrayList<>(loader.getNetexEntitiesIndex().getFareZoneIndex().getLatestVersions());
    }

    private byte[] write(Object body, Type type) throws Exception {
        MockHttpOutputMessage message = new MockHttpOutputMessage();
        converter.write(body, type, MediaType.APPLICATION_JSON, message);
        return message.getBodyAsBytes();
    }

    @Test
    void writesStopPlaceListByteIdenticallyToJackson() throws Exception {
        List<StopPlace> body = stopPlaces();
        assertArrayEquals(
                netexJsonObjectMapper.get().writeValueAsBytes(body),
                write(body, listOf(StopPlace.class)));
    }

    @Test
    void writesQuayListByteIdenticallyToJackson() throws Exception {
        List<Quay> body = quays();
        assertArrayEquals(
                netexJsonObjectMapper.get().writeValueAsBytes(body),
                write(body, listOf(Quay.class)));
    }

    @Test
    void writesTopographicPlaceListByteIdenticallyToJackson() throws Exception {
        List<TopographicPlace> body = topographicPlaces();
        assertFalse(body.isEmpty(), "fixture precondition: the index has topographic places");
        assertArrayEquals(
                netexJsonObjectMapper.get().writeValueAsBytes(body),
                write(body, listOf(TopographicPlace.class)));
    }

    @Test
    void handlesTopographicPlacesAsJson() {
        assertTrue(converter.canWrite(listOf(TopographicPlace.class), List.class, MediaType.APPLICATION_JSON));
        assertTrue(converter.canWrite(TopographicPlace.class, TopographicPlace.class, MediaType.APPLICATION_JSON));
    }

    @Test
    void writesSingleStopPlaceByteIdenticallyToJackson() throws Exception {
        StopPlace body = stopPlaces().getFirst();
        assertArrayEquals(
                netexJsonObjectMapper.get().writeValueAsBytes(body),
                write(body, StopPlace.class));
    }

    @Test
    void writesEmptyListAsEmptyJsonArray() throws Exception {
        assertArrayEquals("[]".getBytes(), write(List.of(), listOf(StopPlace.class)));
    }

    @Test
    void handlesStopPlacesAndQuaysAsJson() {
        assertTrue(converter.canWrite(listOf(StopPlace.class), List.class, MediaType.APPLICATION_JSON));
        assertTrue(converter.canWrite(listOf(Quay.class), List.class, MediaType.APPLICATION_JSON));
        assertTrue(converter.canWrite(StopPlace.class, StopPlace.class, MediaType.APPLICATION_JSON));
        assertTrue(converter.canWrite(Quay.class, Quay.class, MediaType.APPLICATION_JSON));
    }

    @Test
    void handlesTariffZonesAndFareZonesAsJson() {
        assertTrue(converter.canWrite(listOf(TariffZone.class), List.class, MediaType.APPLICATION_JSON));
        assertTrue(converter.canWrite(listOf(FareZone.class), List.class, MediaType.APPLICATION_JSON));
        assertTrue(converter.canWrite(TariffZone.class, TariffZone.class, MediaType.APPLICATION_JSON));
        assertTrue(converter.canWrite(FareZone.class, FareZone.class, MediaType.APPLICATION_JSON));
    }

    @Test
    void writesTariffZoneListByteIdenticallyToJackson() throws Exception {
        List<TariffZone> body = tariffZones();
        assertFalse(body.isEmpty(), "fixture precondition: the index has tariff zones");
        assertArrayEquals(
                netexJsonObjectMapper.get().writeValueAsBytes(body),
                write(body, listOf(TariffZone.class)));
    }

    @Test
    void writesFareZoneListByteIdenticallyToJackson() throws Exception {
        List<FareZone> body = fareZones();
        assertFalse(body.isEmpty(), "fixture precondition: the index has fare zones");
        assertArrayEquals(
                netexJsonObjectMapper.get().writeValueAsBytes(body),
                write(body, listOf(FareZone.class)));
    }

    /**
     * Parking stands in for every type the converter does not claim. Keeping one
     * such type asserted is what stops the converter quietly widening to
     * responses it cannot render — it used to be TariffZone, which is now
     * pre-rendered itself.
     */
    @Test
    void leavesOtherEntityTypesToJackson() {
        assertFalse(converter.canWrite(listOf(Parking.class), List.class, MediaType.APPLICATION_JSON));
        assertFalse(converter.canWrite(Parking.class, Parking.class, MediaType.APPLICATION_JSON));
    }

    @Test
    void leavesXmlToTheNetexConverter() {
        assertFalse(converter.canWrite(listOf(StopPlace.class), List.class, MediaType.APPLICATION_XML));
        assertFalse(converter.canWrite(StopPlace.class, StopPlace.class, MediaType.APPLICATION_XML));
    }
}
