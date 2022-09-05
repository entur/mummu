package no.entur.mummu.serializers;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

@Component
public class CustomSerializers extends SimpleSerializers {
    private final MummuSerializerContext mummuSerializerContext;
    private final JAXBElementSerializer jaxbElementSerializer = new JAXBElementSerializer();

    private final Type jaxbelementType = TypeFactory.defaultInstance().constructType(JAXBElement.class).getRawClass();
    private final Type localDateTimeType = TypeFactory.defaultInstance().constructType(LocalDateTime.class);

    @Autowired
    public CustomSerializers(MummuSerializerContext mummuSerializerContext) {
        this.mummuSerializerContext = mummuSerializerContext;
    }

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        if (isJaxbelementType(type)) {
            return jaxbElementSerializer;
        }

        if (isLocalDateTimeType(type)) {
            return new LocalDateTimeWithConstantZoneSerializer(mummuSerializerContext);
        }

        if (type.isEnumType()) {
            return new LowerCaseEnumValueSerializer();
        }

        return super.findSerializer(config, type, beanDesc);
    }

    private boolean isJaxbelementType(JavaType type) {
        return type.getRawClass().equals(jaxbelementType);
    }

    private boolean isLocalDateTimeType(JavaType type) {
        return type.equals(localDateTimeType);
    }
}
