package no.entur.mummu.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.io.Serializable;

public class JAXBElementSerializer extends JsonSerializer<JAXBElement<?>> implements Serializable {
    private final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void serialize(JAXBElement<?> jaxbElement, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            try {
                jsonGenerator.writeObject(
                        jaxbElement.getValue()
                );
            } catch (IOException e) {
                logger.warn("Caught exception while serializing JAXBElement={}", jaxbElement, e);
            }
    }
}
