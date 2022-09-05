package no.entur.mummu.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class LowerCaseEnumValueSerializer extends JsonSerializer<Enum<?>> {
    @Override
    public void serialize(Enum<?> anEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(anEnum.toString().toLowerCase());
    }
}
