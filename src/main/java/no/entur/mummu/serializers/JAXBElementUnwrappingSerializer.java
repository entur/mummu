package no.entur.mummu.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.xml.bind.JAXBElement;

import java.io.IOException;
import java.util.List;

public class JAXBElementUnwrappingSerializer extends JsonSerializer<List<JAXBElement<?>>> {

    @Override
    public void serialize(List<JAXBElement<?>> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartArray();
        for (JAXBElement<?> element : value) {
            gen.writeObject(element.getValue());
        }
        gen.writeEndArray();
    }
}
