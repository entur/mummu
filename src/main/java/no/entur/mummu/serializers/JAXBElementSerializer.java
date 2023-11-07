package no.entur.mummu.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.Serializable;

public class JAXBElementSerializer extends JsonSerializer<JAXBElement<?>> implements Serializable {

    @Override
    public void serialize(JAXBElement<?> jaxbElement, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("type", jaxbElement.getName().getLocalPart());
        jsonGenerator.writeObjectField("value", jaxbElement.getValue());
        jsonGenerator.writeEndObject();
    }

    @Override
    public void serializeWithType(JAXBElement<?> jaxbElement, JsonGenerator jsonGenerator, SerializerProvider serializerProvider, TypeSerializer typeSer) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("type", jaxbElement.getName().getLocalPart());
        jsonGenerator.writeObjectField("value", jaxbElement.getValue());
        jsonGenerator.writeEndObject();
    }
}