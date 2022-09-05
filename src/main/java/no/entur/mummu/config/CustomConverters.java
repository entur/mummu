package no.entur.mummu.config;

import com.fasterxml.jackson.databind.JavaType;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverter;
import io.swagger.v3.core.converter.ModelConverterContext;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.media.Schema;

import javax.xml.bind.JAXBElement;
import java.util.Iterator;

public class CustomConverters implements ModelConverter {
    @Override
    public Schema resolve(AnnotatedType annotatedType, ModelConverterContext modelConverterContext, Iterator<ModelConverter> iterator) {
        if (annotatedType.isSchemaProperty()) {
            JavaType type = Json.mapper().constructType(annotatedType.getType());
            if (type != null) {
                Class<?> cls = type.getRawClass();
                if (JAXBElement.class.isAssignableFrom(cls)) {
                    return new JAXBElementSchema();
                }
                if (cls.isEnum()) {
                    return new EnumSchema((Class<? extends Enum>) cls);
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
