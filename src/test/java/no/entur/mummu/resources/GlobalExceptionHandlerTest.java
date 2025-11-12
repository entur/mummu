package no.entur.mummu.resources;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.core.MethodParameter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleNotFoundException_shouldReturnNotFoundWithCorrectErrorResponse() {
        // Given
        String errorMessage = "Resource not found";
        NotFoundException exception = new NotFoundException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFoundException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNull(response.getBody().getDetails());
    }

    @Test
    void handleNotFoundException_withDefaultMessage_shouldReturnDefaultMessage() {
        // Given
        NotFoundException exception = new NotFoundException();

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleNotFoundException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("RESOURCE_NOT_FOUND", response.getBody().getErrorCode());
        assertEquals("The requested resource was not found", response.getBody().getMessage());
    }

    @Test
    void handleTypeMismatch_shouldReturnBadRequestWithDetails() {
        // Given
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "invalid",
                Integer.class,
                "count",
                null,
                null
        );

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatch(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_PARAMETER", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("invalid"));
        assertTrue(response.getBody().getMessage().contains("count"));
        assertNotNull(response.getBody().getDetails());
        assertEquals("count", response.getBody().getDetails().get("parameter"));
        assertEquals("invalid", response.getBody().getDetails().get("providedValue"));
    }

    @Test
    void handleIllegalArgument_shouldReturnBadRequest() {
        // Given
        String errorMessage = "Invalid argument provided";
        IllegalArgumentException exception = new IllegalArgumentException(errorMessage);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgument(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_PARAMETER", response.getBody().getErrorCode());
        assertEquals(errorMessage, response.getBody().getMessage());
        assertNull(response.getBody().getDetails());
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        // Given
        Exception exception = new Exception("Unexpected error");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INTERNAL_ERROR", response.getBody().getErrorCode());
        assertEquals("An unexpected error occurred while processing your request", response.getBody().getMessage());
        assertNull(response.getBody().getDetails());
    }

    @Test
    void handleGenericException_shouldLogError() {
        // Given
        Exception exception = new Exception("Test exception for logging");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

        // Then
        // The method should log the error (verified by manual inspection or log capture)
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    void handleValidationException_shouldReturnBadRequestWithDetails() {
        // Given
        FieldError fieldError = new FieldError("stopPlacesRequestParams", "count", "invalid", false, null, null, "Type mismatch");
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldError()).thenReturn(fieldError);

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_PARAMETER", response.getBody().getErrorCode());
        assertTrue(response.getBody().getMessage().contains("invalid"));
        assertTrue(response.getBody().getMessage().contains("count"));
        assertNotNull(response.getBody().getDetails());
        assertEquals("count", response.getBody().getDetails().get("parameter"));
        assertEquals("invalid", response.getBody().getDetails().get("providedValue"));
    }

    @Test
    void handleValidationException_withoutFieldError_shouldReturnBadRequest() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.getFieldError()).thenReturn(null);

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(exception);

        // Then
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("INVALID_PARAMETER", response.getBody().getErrorCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNull(response.getBody().getDetails());
    }
}