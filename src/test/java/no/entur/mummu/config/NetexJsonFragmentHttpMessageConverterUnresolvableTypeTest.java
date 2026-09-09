package no.entur.mummu.config;

import no.entur.mummu.serializers.NetexJsonFragmentCache;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Spring asks every registered converter whether it can write each response, so
 * canWrite has to answer for types this converter knows nothing about — including
 * ones ResolvableType cannot resolve at all, such as the body of a raw
 * {@code ResponseEntity}. It must answer false, not throw.
 */
class NetexJsonFragmentHttpMessageConverterUnresolvableTypeTest {

    private final NetexJsonFragmentHttpMessageConverter converter =
            new NetexJsonFragmentHttpMessageConverter(Mockito.mock(NetexJsonFragmentCache.class));

    /** A handler returning a raw ResponseEntity, as framework endpoints do. */
    @SuppressWarnings("rawtypes")
    public ResponseEntity rawResponseEntity() {
        return ResponseEntity.ok().build();
    }

    /** Mirrors AbstractMessageConverterMethodProcessor.getGenericType. */
    private static Type springTargetTypeOf(String methodName) throws Exception {
        Method method = NetexJsonFragmentHttpMessageConverterUnresolvableTypeTest.class.getMethod(methodName);
        MethodParameter returnType = new MethodParameter(method, -1);
        if (HttpEntity.class.isAssignableFrom(returnType.getParameterType())) {
            return ResolvableType.forType(returnType.getGenericParameterType()).getGeneric().getType();
        }
        return returnType.getGenericParameterType();
    }

    @Test
    void answersFalseForABodyTypeItCannotResolve() throws Exception {
        Type target = springTargetTypeOf("rawResponseEntity");
        assertNull(ResolvableType.forType(target).resolve(),
                "precondition: this is a type ResolvableType cannot resolve");

        assertDoesNotThrow(() -> converter.canWrite(target, Object.class, MediaType.APPLICATION_JSON));
        assertFalse(converter.canWrite(target, Object.class, MediaType.APPLICATION_JSON));
    }

    @Test
    void answersFalseForACollectionWithNoResolvableElementType() {
        // A raw Collection: the element type resolves to null.
        Type rawList = List.class;
        assertDoesNotThrow(() -> converter.canWrite(rawList, List.class, MediaType.APPLICATION_JSON));
        assertFalse(converter.canWrite(rawList, List.class, MediaType.APPLICATION_JSON));
    }
}
