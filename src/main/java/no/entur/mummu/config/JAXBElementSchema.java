package no.entur.mummu.config;

import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;

import javax.xml.bind.JAXBElement;
import java.util.Map;

public class JAXBElementSchema extends Schema<JAXBElement<?>> {
    public JAXBElementSchema() {
        setProperties(Map.of(
                "type", new StringSchema(),
                "value", new ObjectSchema()
        ));
    }
}
