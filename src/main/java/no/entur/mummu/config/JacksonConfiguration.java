package no.entur.mummu.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import no.entur.mummu.serializers.CustomSerializers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.ArrayList;

@Configuration
public class JacksonConfiguration {
    private final CustomSerializers customSerializers;

    @Autowired
    public JacksonConfiguration(CustomSerializers customSerializers) {
        this.customSerializers = customSerializers;
    }

    @Bean
    public ObjectMapper jsonObjectMapper() {
        ArrayList<Module> modules = new ArrayList<>();
        var customSerializersModule = new SimpleModule();
        customSerializersModule.setSerializers(customSerializers);
        modules.add(customSerializersModule);
        return Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(modules)
                .build();
    }
}
