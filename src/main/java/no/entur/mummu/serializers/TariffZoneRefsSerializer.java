package no.entur.mummu.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;

import java.io.IOException;

public class TariffZoneRefsSerializer extends JsonSerializer<TariffZoneRefs_RelStructure> {
    @Override
    public void serialize(TariffZoneRefs_RelStructure tariffZoneRefsRelStructure, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartArray();
        for (var tariffZone :tariffZoneRefsRelStructure.getTariffZoneRef_()) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("ref", tariffZone.getValue().getRef());
            jsonGenerator.writeObjectField("version", tariffZone.getValue().getVersion());
            jsonGenerator.writeEndObject();
        }
        jsonGenerator.writeEndArray();
    }
}
