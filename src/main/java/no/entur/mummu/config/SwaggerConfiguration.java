package no.entur.mummu.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.jackson.ModelResolver;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
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
        return new OpenAPI()
            .servers(List.of(server))
            .info(new Info()
                .title("Stop Place Register")
                .version("1.0.0")
                .description("The Stop Place Register provides access to public transportation infrastructure data across Norway, including stop places, quays, parkings, and related NeTEx entities. This API enables developers to query stop place information with details on location, accessibility features, transport modes, fare zones, and hierarchical relationships. Ideal for journey planning applications, transportation analysis, mobility services, and public transit integrations.")
                .contact(new Contact()
                    .name("Entur API Support")
                    .url("https://developer.entur.org")))
            .tags(List.of(
                new Tag()
                    .name("Stop Places")
                    .description("Query and retrieve stop place information including stations, terminals, bus stops, and ferry ports. Includes filtering by transport mode, location, and other attributes."),
                new Tag()
                    .name("Quays")
                    .description("Access detailed information about quays (platforms, boarding positions) and their relationship to stop places. Query by ID or retrieve version history."),
                new Tag()
                    .name("Scheduled Stop Points")
                    .description("Retrieve scheduled stop points used in route planning and timetables, and their mappings to physical stop places."),
                new Tag()
                    .name("Fare Zones")
                    .description("Access fare zone definitions and boundaries used for ticket pricing calculations. Query by authority or specific zones."),
                new Tag()
                    .name("Parking")
                    .description("Find parking facilities associated with stop places, including capacity, pricing, and accessibility information."),
                new Tag()
                    .name("Geographic Areas")
                    .description("Query topographic places (municipalities, counties, countries) and tariff zones for geographic and administrative boundaries."),
                new Tag()
                    .name("Groupings")
                    .description("Access logical groupings of stop places and fare zones for organizational and operational purposes.")
            ));
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
