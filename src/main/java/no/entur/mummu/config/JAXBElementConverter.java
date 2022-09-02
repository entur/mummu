package no.entur.mummu.config;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;

import javax.xml.bind.JAXBElement;
import java.util.Iterator;

public class JAXBElementConverter implements ModelConverter {
    @Override
    public Schema resolve(AnnotatedType annotatedType, ModelConverterContext modelConverterContext, Iterator<ModelConverter> iterator) {
        if (annotatedType.isSchemaProperty()) {
            JavaType _type = Json.mapper().constructType(annotatedType.getType());
            if (_type != null) {
                Class<?> cls = _type.getRawClass();
                if (JAXBElement.class.isAssignableFrom(cls)) {
                    return new JAXBElementSchema();
                }
            }
        }
        if (iterator.hasNext()) {
            return iterator.next().resolve(annotatedType, modelConverterContext, iterator);
        } else {
            return null;
        }
    }
}
