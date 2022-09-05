package no.entur.mummu.config;

import io.swagger.v3.oas.models.media.StringSchema;

import javax.xml.bind.annotation.XmlElement;
import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumSchema extends StringSchema {
    public EnumSchema(Class<? extends Enum> enumType) {
        super();

        // FML
        // the XmlElement annotation is not on the enum class but on
        // the individual field declarations using the enums
        //setDefault(enumType.getAnnotation(XmlElement.class).defaultValue());

        setEnum(Arrays.stream(enumType.getEnumConstants()).map(Enum::toString).map(String::toLowerCase).collect(Collectors.toList()));

    }
}
