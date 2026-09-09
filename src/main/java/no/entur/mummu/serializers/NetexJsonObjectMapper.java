package no.entur.mummu.serializers;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.rutebanken.netex.model.ParkingAreaRefs_RelStructure;
import org.rutebanken.netex.model.ParkingAreas_RelStructure;
import org.rutebanken.netex.model.Quays_RelStructure;
import org.rutebanken.netex.model.StopPlaceRefs_RelStructure;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds the single {@link ObjectMapper} used for every NeTEx JSON response.
 * <p>
 * Both the request-time {@code MappingJackson2HttpMessageConverter} and the
 * pre-rendered fragments in {@link NetexJsonFragmentCache} serialize through
 * this one instance, which is what makes a composed fragment response
 * byte-identical to serializing the same entities on the fly.
 * <p>
 * Deliberately not an {@code ObjectMapper} bean: declaring one would make
 * Spring Boot's Jackson auto-configuration back off and change the mapper used
 * elsewhere in the application.
 */
@Component
public class NetexJsonObjectMapper {

    private final ObjectMapper objectMapper;

    public NetexJsonObjectMapper(CustomSerializers customSerializers) {
        this.objectMapper = build(customSerializers);
    }

    public ObjectMapper get() {
        return objectMapper;
    }

    private static ObjectMapper build(CustomSerializers customSerializers) {
        List<Module> modules = new ArrayList<>();
        var customSerializersModule = new SimpleModule();
        customSerializersModule.setSerializers(customSerializers);
        modules.add(customSerializersModule);
        return Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_EMPTY)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .featuresToEnable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                // Serialize via fields, not getters. The NeTEx model getters lazy-initialize
                // collection fields (null -> empty list) on first call. Because the index holds
                // shared entity instances, getter-based JSON serialization permanently mutates
                // them; a later XML (JAXB) response then marshals those empty lists as empty
                // elements (e.g. <CardsAccepted></CardsAccepted>), which is invalid NeTEx.
                // Reading fields directly avoids triggering the getters and keeps the shared
                // model immutable across requests. Explicitly annotated getters (the mixins)
                // are still honoured regardless of visibility.
                .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .visibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE)
                .visibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE)
                .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .modules(modules)
                .mixIn(Quays_RelStructure.class, NetexJsonMixins.QuaysRelStructureMixin.class)
                .mixIn(TariffZoneRefs_RelStructure.class, NetexJsonMixins.TariffZoneRefsRelStructureMixin.class)
                .mixIn(ParkingAreas_RelStructure.class, NetexJsonMixins.ParkingAreasRelStructureMixin.class)
                .mixIn(StopPlaceRefs_RelStructure.class, NetexJsonMixins.StopPlaceRefsRelStructureMixin.class)
                .mixIn(ParkingAreaRefs_RelStructure.class, NetexJsonMixins.ParkingAreaRefsRelStructureMixin.class)
                .build();
    }
}
