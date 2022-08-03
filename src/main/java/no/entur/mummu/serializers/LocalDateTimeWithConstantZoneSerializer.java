package no.entur.mummu.serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class LocalDateTimeWithConstantZoneSerializer extends JsonSerializer<LocalDateTime> {
    private final ZonedDateTimeSerializer zonedDateTimeSerializer = ZonedDateTimeSerializer.INSTANCE;
    private final LocalDateTimeSerializer localDateTimeSerializer = LocalDateTimeSerializer.INSTANCE;
    private final MummuSerializerContext mummuSerializerContext;

    public LocalDateTimeWithConstantZoneSerializer(
            MummuSerializerContext mummuSerializerContext
    ) {
        this.mummuSerializerContext = mummuSerializerContext;
    }

    @Override
    public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        if (mummuSerializerContext.getZoneId() != null) {
            zonedDateTimeSerializer.serialize(localDateTime.atZone(mummuSerializerContext.getZoneId()), jsonGenerator, serializerProvider);
        } else {
            localDateTimeSerializer.serialize(localDateTime, jsonGenerator, serializerProvider);
        }
    }
}
