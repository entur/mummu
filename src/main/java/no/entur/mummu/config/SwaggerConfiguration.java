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
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    public OpenApiCustomizer netexModelCompatibilityCustomizer() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            if (schemas == null) return;

            // Fix TariffZoneRefs_RelStructure: rename tariffZoneRef_ back to tariffZoneRef
            Schema tariffZoneRefs = schemas.get("TariffZoneRefs_RelStructure");
            if (tariffZoneRefs != null && tariffZoneRefs.getProperties() != null) {
                Map<String, Schema> props = tariffZoneRefs.getProperties();
                if (props.containsKey("tariffZoneRef_")) {
                    Schema tariffZoneRefProp = props.remove("tariffZoneRef_");
                    // Restore original schema: array of TariffZoneRef with XML name
                    Schema restoredProp = new ArraySchema()
                            .items(new Schema<>().$ref("#/components/schemas/TariffZoneRef"));
                    restoredProp.xml(new io.swagger.v3.oas.models.media.XML().name("TariffZoneRef"));
                    props.put("tariffZoneRef", restoredProp);
                    // Re-sort properties alphabetically
                    Map<String, Schema> sorted = new LinkedHashMap<>();
                    props.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(e -> sorted.put(e.getKey(), e.getValue()));
                    tariffZoneRefs.setProperties(sorted);
                    // Restore required field
                    tariffZoneRefs.setRequired(List.of("tariffZoneRef"));
                }
            }

            // Fix Quays_RelStructure: restore quayRefOrQuay items to empty schema
            Schema quaysRel = schemas.get("Quays_RelStructure");
            if (quaysRel != null && quaysRel.getProperties() != null) {
                Map<String, Schema> props = quaysRel.getProperties();
                Schema quayProp = props.get("quayRefOrQuay");
                if (quayProp != null && quayProp.getItems() != null) {
                    quayProp.setItems(new Schema<>());
                }
            }

            // Fix ParkingAreas_RelStructure: rename parkingAreaRefOrParkingArea_ back
            renameProperty(schemas, "ParkingAreas_RelStructure",
                    "parkingAreaRefOrParkingArea_", "parkingAreaRefOrParkingArea");

            // Fix StopPlacesInFrame_RelStructure: rename stopPlace_ back to stopPlace
            Schema stopPlacesInFrame = schemas.get("StopPlacesInFrame_RelStructure");
            if (stopPlacesInFrame != null && stopPlacesInFrame.getProperties() != null) {
                Map<String, Schema> spProps = stopPlacesInFrame.getProperties();
                if (spProps.containsKey("stopPlace_")) {
                    spProps.remove("stopPlace_");
                    Schema stopPlaceProp = new ArraySchema()
                            .items(new Schema<>().$ref("#/components/schemas/StopPlace"));
                    stopPlaceProp.xml(new io.swagger.v3.oas.models.media.XML().name("StopPlace"));
                    spProps.put("stopPlace", stopPlaceProp);
                    // Re-sort alphabetically
                    Map<String, Schema> sorted = new LinkedHashMap<>();
                    spProps.entrySet().stream()
                            .sorted(Map.Entry.comparingByKey())
                            .forEach(e -> sorted.put(e.getKey(), e.getValue()));
                    stopPlacesInFrame.setProperties(sorted);
                    stopPlacesInFrame.setRequired(List.of("stopPlace"));
                }
            }

            // Fix StopPlaceRefs_RelStructure: restore items ref and required
            restoreJaxbElementArrayProp(schemas, "StopPlaceRefs_RelStructure",
                    "stopPlaceRef", "StopPlaceRefStructure", "StopPlaceRef");

            // Fix ParkingAreaRefs_RelStructure: restore items ref and required
            restoreJaxbElementArrayProp(schemas, "ParkingAreaRefs_RelStructure",
                    "parkingAreaRef", "ParkingAreaRefStructure", "ParkingAreaRef");

            // Restore TariffZoneRef schema (removed in new model, still referenced)
            if (!schemas.containsKey("TariffZoneRef")) {
                schemas.put("TariffZoneRef", schemas.getOrDefault("ZoneRefStructure", new Schema<>()));
            }
        };
    }

    private static void restoreJaxbElementArrayProp(Map<String, Schema> schemas,
                                                     String schemaName, String propName,
                                                     String itemsRef, String xmlName) {
        Schema schema = schemas.get(schemaName);
        if (schema != null && schema.getProperties() != null) {
            Map<String, Schema> props = schema.getProperties();
            Schema prop = props.get(propName);
            if (prop != null) {
                prop.setItems(new Schema<>().$ref("#/components/schemas/" + itemsRef));
                prop.xml(new io.swagger.v3.oas.models.media.XML().name(xmlName));
                schema.setRequired(List.of(propName));
            }
        }
    }

    private static void renameProperty(Map<String, Schema> schemas, String schemaName,
                                        String oldPropName, String newPropName) {
        Schema schema = schemas.get(schemaName);
        if (schema != null && schema.getProperties() != null) {
            Map<String, Schema> props = schema.getProperties();
            if (props.containsKey(oldPropName)) {
                Schema prop = props.remove(oldPropName);
                // Reset items to empty schema (unwrapped JAXBElements)
                if (prop.getItems() != null) {
                    prop.setItems(new Schema<>());
                }
                props.put(newPropName, prop);
                // Re-sort alphabetically
                Map<String, Schema> sorted = new LinkedHashMap<>();
                props.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(e -> sorted.put(e.getKey(), e.getValue()));
                schema.setProperties(sorted);
            }
        }
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
