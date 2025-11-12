package no.entur.mummu.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testDefaultConstructor() {
        // When
        ErrorResponse errorResponse = new ErrorResponse();

        // Then
        assertNotNull(errorResponse);
        assertNull(errorResponse.getErrorCode());
        assertNull(errorResponse.getMessage());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testConstructorWithCodeAndMessage() {
        // Given
        String errorCode = "TEST_ERROR";
        String message = "Test error message";

        // When
        ErrorResponse errorResponse = new ErrorResponse(errorCode, message);

        // Then
        assertNotNull(errorResponse);
        assertEquals(errorCode, errorResponse.getErrorCode());
        assertEquals(message, errorResponse.getMessage());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testConstructorWithAllFields() {
        // Given
        String errorCode = "TEST_ERROR";
        String message = "Test error message";
        Map<String, Object> details = Map.of(
                "field", "testField",
                "value", "testValue",
                "count", 42
        );

        // When
        ErrorResponse errorResponse = new ErrorResponse(errorCode, message, details);

        // Then
        assertNotNull(errorResponse);
        assertEquals(errorCode, errorResponse.getErrorCode());
        assertEquals(message, errorResponse.getMessage());
        assertNotNull(errorResponse.getDetails());
        assertEquals(3, errorResponse.getDetails().size());
        assertEquals("testField", errorResponse.getDetails().get("field"));
        assertEquals("testValue", errorResponse.getDetails().get("value"));
        assertEquals(42, errorResponse.getDetails().get("count"));
    }

    @Test
    void testSetters() {
        // Given
        ErrorResponse errorResponse = new ErrorResponse();
        String errorCode = "UPDATED_ERROR";
        String message = "Updated message";
        Map<String, Object> details = Map.of("key", "value");

        // When
        errorResponse.setErrorCode(errorCode);
        errorResponse.setMessage(message);
        errorResponse.setDetails(details);

        // Then
        assertEquals(errorCode, errorResponse.getErrorCode());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(details, errorResponse.getDetails());
    }

    @Test
    void testJsonSerialization_withoutDetails() throws Exception {
        // Given
        ErrorResponse errorResponse = new ErrorResponse("NOT_FOUND", "Resource not found");

        // When
        String json = objectMapper.writeValueAsString(errorResponse);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"errorCode\":\"NOT_FOUND\""));
        assertTrue(json.contains("\"message\":\"Resource not found\""));
        assertFalse(json.contains("\"details\""));
    }

    @Test
    void testJsonSerialization_withDetails() throws Exception {
        // Given
        Map<String, Object> details = Map.of("id", "123", "type", "StopPlace");
        ErrorResponse errorResponse = new ErrorResponse("NOT_FOUND", "Resource not found", details);

        // When
        String json = objectMapper.writeValueAsString(errorResponse);

        // Then
        assertNotNull(json);
        assertTrue(json.contains("\"errorCode\":\"NOT_FOUND\""));
        assertTrue(json.contains("\"message\":\"Resource not found\""));
        assertTrue(json.contains("\"details\""));
        assertTrue(json.contains("\"id\":\"123\""));
        assertTrue(json.contains("\"type\":\"StopPlace\""));
    }

    @Test
    void testJsonDeserialization_withoutDetails() throws Exception {
        // Given
        String json = "{\"errorCode\":\"NOT_FOUND\",\"message\":\"Resource not found\"}";

        // When
        ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);

        // Then
        assertNotNull(errorResponse);
        assertEquals("NOT_FOUND", errorResponse.getErrorCode());
        assertEquals("Resource not found", errorResponse.getMessage());
        assertNull(errorResponse.getDetails());
    }

    @Test
    void testJsonDeserialization_withDetails() throws Exception {
        // Given
        String json = "{\"errorCode\":\"INVALID_PARAMETER\",\"message\":\"Invalid value\",\"details\":{\"parameter\":\"count\",\"value\":\"abc\"}}";

        // When
        ErrorResponse errorResponse = objectMapper.readValue(json, ErrorResponse.class);

        // Then
        assertNotNull(errorResponse);
        assertEquals("INVALID_PARAMETER", errorResponse.getErrorCode());
        assertEquals("Invalid value", errorResponse.getMessage());
        assertNotNull(errorResponse.getDetails());
        assertEquals("count", errorResponse.getDetails().get("parameter"));
        assertEquals("abc", errorResponse.getDetails().get("value"));
    }

    @Test
    void testJsonIncludeNonNull() throws Exception {
        // Given
        ErrorResponse errorResponse = new ErrorResponse();
        errorResponse.setErrorCode("ERROR");
        errorResponse.setMessage("Message");
        // details is null

        // When
        String json = objectMapper.writeValueAsString(errorResponse);

        // Then
        assertNotNull(json);
        assertFalse(json.contains("\"details\""), "Null details should not be included in JSON");
    }
}

