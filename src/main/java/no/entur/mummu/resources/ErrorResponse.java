package no.entur.mummu.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@Schema(description = "Error response returned when a request fails")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "Machine-readable error code", example = "RESOURCE_NOT_FOUND")
    private String errorCode;

    @Schema(description = "Human-readable error message", example = "Resource not found")
    private String message;

    @Schema(description = "Additional error context")
    private Map<String, Object> details;

    public ErrorResponse() {}

    public ErrorResponse(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorResponse(String errorCode, String message, Map<String, Object> details) {
        this.errorCode = errorCode;
        this.message = message;
        this.details = details;
    }

    // Getters and setters
    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}