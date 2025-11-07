package no.entur.mummu.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

    static {
        // Configure swagger-core's global JSON mapper for OpenAPI serialization
        // This must be done in a static block to run before any OpenAPI processing
        ObjectMapper globalMapper = Json.mapper();
        globalMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        globalMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
    }

    @Value("${no.entur.mummu.swagger.host.url}")
    private String hostUrl;

    public SwaggerConfiguration() {
        // Configure ObjectMapper with sorted properties for OpenAPI spec generation
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

        // Configure swagger-core's JSON mapper for schema generation
        ModelConverters.getInstance().addConverter(new ModelResolver(objectMapper));
        ModelConverters.getInstance().addConverter(new CustomConverters());
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(hostUrl);
        return new OpenAPI().servers(List.of(server));
    }

    @Bean
    public ObjectMapperProvider springDocObjectMapperProvider() {
        return new ObjectMapperProvider(new SpringDocConfigProperties()) {
            @Override
            public ObjectMapper jsonMapper() {
                ObjectMapper mapper = super.jsonMapper();
                mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
                mapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
                return mapper;
            }
        };
    }
}
