package no.entur.mummu.serializers;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleSerializers;
import com.fasterxml.jackson.databind.type.TypeFactory;

import javax.xml.bind.JAXBElement;
import java.lang.reflect.Type;

public class CustomSerializers extends SimpleSerializers {

    @Override
    public JsonSerializer<?> findSerializer(SerializationConfig config, JavaType type, BeanDescription beanDesc) {
        if (isJaxbelementType(type)) {
            return new JAXBElementSerializer();
        }

        return super.findSerializer(config, type, beanDesc);
    }

    private boolean isJaxbelementType(JavaType type) {
        Type jaxbelementListType = TypeFactory.defaultInstance().constructType(JAXBElement.class).getRawClass();
        return type.getRawClass().equals(jaxbelementListType);
    }
}
