package no.entur.mummu.resources;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void testDefaultConstructor() {
        // When
        NotFoundException exception = new NotFoundException();

        // Then
        assertNotNull(exception);
        assertEquals("The requested resource was not found", exception.getMessage());
    }

    @Test
    void testConstructorWithMessage() {
        // Given
        String customMessage = "Stop place with ID NSR:StopPlace:123 not found";

        // When
        NotFoundException exception = new NotFoundException(customMessage);

        // Then
        assertNotNull(exception);
        assertEquals(customMessage, exception.getMessage());
    }

    @Test
    void testIsRuntimeException() {
        // When
        NotFoundException exception = new NotFoundException();

        // Then
        assertTrue(exception instanceof RuntimeException);
    }

    @Test
    void testResponseStatusAnnotation() {
        // When
        ResponseStatus annotation = NotFoundException.class.getAnnotation(ResponseStatus.class);

        // Then
        assertNotNull(annotation, "NotFoundException should have @ResponseStatus annotation");
        assertEquals(HttpStatus.NOT_FOUND, annotation.code());
    }

    @Test
    void testExceptionCanBeThrown() {
        // Given
        String errorMessage = "Resource not found";

        // When/Then
        Exception exception = assertThrows(NotFoundException.class, () -> {
            throw new NotFoundException(errorMessage);
        });

        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testExceptionWithDefaultMessageCanBeThrown() {
        // When/Then
        Exception exception = assertThrows(NotFoundException.class, () -> {
            throw new NotFoundException();
        });

        assertEquals("The requested resource was not found", exception.getMessage());
    }

    @Test
    void testExceptionStackTrace() {
        // Given
        NotFoundException exception = new NotFoundException("Test exception");

        // When
        StackTraceElement[] stackTrace = exception.getStackTrace();

        // Then
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
    }
}

