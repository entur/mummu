package no.entur.mummu.config;

import io.swagger.v3.oas.models.media.StringSchema;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumSchema extends StringSchema {
    public EnumSchema(Class<? extends Enum> enumType) {
        super();
        setEnum(Arrays.stream(enumType.getEnumConstants()).map(Enum::toString).collect(Collectors.toList()));
    }
}
