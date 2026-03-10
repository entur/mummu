package no.entur.mummu.serializers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.ZoneRefStructure;

import java.util.List;

public class NetexJsonMixins {

    public abstract static class QuaysRelStructureMixin {
        @JsonSerialize(using = JAXBElementUnwrappingSerializer.class)
        @JsonProperty("quayRefOrQuay")
        public abstract List<JAXBElement<?>> getQuayRefOrQuay();
    }

    @SuppressWarnings("rawtypes")
    public abstract static class TariffZoneRefsRelStructureMixin {
        @JsonSerialize(using = JAXBElementUnwrappingSerializer.class)
        @JsonProperty("tariffZoneRef")
        public abstract List getTariffZoneRef_();
    }

    public abstract static class ParkingAreasRelStructureMixin {
        @JsonSerialize(using = JAXBElementUnwrappingSerializer.class)
        @JsonProperty("parkingAreaRefOrParkingArea")
        public abstract List<JAXBElement<?>> getParkingAreaRefOrParkingArea_();
    }
}
