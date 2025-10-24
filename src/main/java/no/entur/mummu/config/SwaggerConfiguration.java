package no.entur.mummu.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.util.List;

@Configuration
public class SwaggerConfiguration {

    @Value("${no.entur.mummu.swagger.host.url}")
    private String hostUrl;

    public SwaggerConfiguration() {
        // Configure ObjectMapper with sorted properties for OpenAPI spec generation
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

        ModelConverters.getInstance().addConverter(new ModelResolver(objectMapper));
        ModelConverters.getInstance().addConverter(new CustomConverters());
    }

    @Bean
    public Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        builder.featuresToEnable(
            SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS,
            MapperFeature.SORT_PROPERTIES_ALPHABETICALLY
        );
        return builder;
    }

    @Bean
    public OpenAPI customOpenAPI() {
        Server server = new Server();
        server.setUrl(hostUrl);
        return new OpenAPI().servers(List.of(server));
    }
}
