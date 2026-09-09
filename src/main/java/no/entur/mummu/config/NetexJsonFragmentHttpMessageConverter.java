package no.entur.mummu.config;

import no.entur.mummu.serializers.NetexJsonFragmentCache;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.rutebanken.netex.model.Quay;
import org.rutebanken.netex.model.StopPlace;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Writes JSON responses for the highest-volume entity types by copying
 * pre-rendered fragments from {@link NetexJsonFragmentCache} instead of walking
 * the object graph with Jackson on every request.
 * <p>
 * A JSON array of entities is a plain concatenation of independent per-entity
 * documents, so {@code '[' + fragments joined by ',' + ']'} reproduces exactly
 * what {@code MappingJackson2HttpMessageConverter} would have produced. This
 * converter is registered ahead of the Jackson one and claims only the types in
 * {@link #PRE_RENDERED_TYPES}; everything else falls through to Jackson
 * unchanged.
 */
@Component
public class NetexJsonFragmentHttpMessageConverter implements GenericHttpMessageConverter<Object> {

    /**
     * The types worth pre-rendering: stop places and quays account for the bulk
     * of the bytes this API serves. Adding a type here costs the heap needed to
     * retain its rendered JSON, so it is a deliberate choice rather than a
     * blanket rule.
     */
    private static final Set<Class<?>> PRE_RENDERED_TYPES = Set.of(StopPlace.class, Quay.class);

    private final NetexJsonFragmentCache fragmentCache;

    public NetexJsonFragmentHttpMessageConverter(NetexJsonFragmentCache fragmentCache) {
        this.fragmentCache = fragmentCache;
    }

    @Override
    public boolean canWrite(Type type, Class<?> clazz, MediaType mediaType) {
        return isJson(mediaType) && PRE_RENDERED_TYPES.contains(entityType(type, clazz));
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return canWrite(clazz, clazz, mediaType);
    }

    @Override
    public void write(Object body, Type type, MediaType contentType, HttpOutputMessage outputMessage)
            throws IOException {
        outputMessage.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        OutputStream out = outputMessage.getBody();
        if (body instanceof Collection<?> collection) {
            writeArray(collection, out);
        } else {
            out.write(fragmentOf(body));
        }
        out.flush();
    }

    @Override
    public void write(Object body, MediaType contentType, HttpOutputMessage outputMessage) throws IOException {
        write(body, body.getClass(), contentType, outputMessage);
    }

    private void writeArray(Collection<?> collection, OutputStream out) throws IOException {
        out.write('[');
        boolean first = true;
        for (Object element : collection) {
            if (!first) {
                out.write(',');
            }
            first = false;
            out.write(fragmentOf(element));
        }
        out.write(']');
    }

    private byte[] fragmentOf(Object element) {
        if (element instanceof EntityInVersionStructure entity) {
            return fragmentCache.fragment(entity);
        }
        throw new HttpMessageNotWritableException("Not a NeTEx entity: " + element.getClass());
    }

    /**
     * The entity type a response body is made of: the element type for a
     * collection, otherwise the type itself.
     */
    private static Class<?> entityType(Type type, Class<?> clazz) {
        ResolvableType resolvable = type != null ? ResolvableType.forType(type) : ResolvableType.forClass(clazz);
        if (Collection.class.isAssignableFrom(resolvable.toClass())) {
            return resolvable.asCollection().getGeneric(0).resolve();
        }
        return resolvable.resolve();
    }

    private static boolean isJson(MediaType mediaType) {
        return mediaType == null || MediaType.APPLICATION_JSON.isCompatibleWith(mediaType);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return List.of(MediaType.APPLICATION_JSON);
    }

    @Override
    public boolean canRead(Type type, Class<?> contextClass, MediaType mediaType) {
        return false;
    }

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return false;
    }

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) {
        throw new UnsupportedOperationException("This converter only writes responses");
    }

    @Override
    public Object read(Class<?> clazz, HttpInputMessage inputMessage) {
        throw new UnsupportedOperationException("This converter only writes responses");
    }
}
