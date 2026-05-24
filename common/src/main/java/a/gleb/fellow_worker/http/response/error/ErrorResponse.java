package a.gleb.fellow_worker.http.response.error;

import a.gleb.fellow_worker.common.FellowWorkerErrorCode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized error response format for microservices.
 * <p>
 * Contains detailed error information for client applications and logging systems.
 * Error codes follow the standard defined in {@link FellowWorkerErrorCode}.
 *
 * @param timestamp    Date and time when the error occurred in UTC
 * @param errorId      Unique error identifier (UUID or other unique identifier)
 * @param errorCode    Error code from {@link FellowWorkerErrorCode} (format "FW-XXXX")
 * @param errorMessage Human-readable error description
 * @param parameters   Additional error context (request parameters, technical details, etc.)
 */
@Schema(description = "Standardized error response format for fellow-worker services")
public record ErrorResponse(
        @Schema(
                description = "Exact time when the error occurred in UTC",
                example = "2024-01-01T12:00:00Z",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        LocalDateTime timestamp,

        @Schema(
                description = "Unique identifier for error tracing",
                example = "550e8400-e29b-41d4-a716-446655440000",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String errorId,

        @Schema(
                description = "Error code according to the fellow-worker service specification",
                example = "FW_4001",
                requiredMode = Schema.RequiredMode.REQUIRED,
                allowableValues = {
                        "FW_4001", "FW_4002", "FW_4003", "FW_4004", "FW_4005",
                        "FW_4006", "FW_4007", "FW_4008", "FW_4009", "FW_4010",
                        "FW_4011", "FW_4012", "FW_4013", "FW_4014", "FW_4015",
                        "FW_4016", "FW_4017", "FW_4018", "FW_5000", "FW_5001",
                        "FW_5002", "FW_5003", "FW_5004", "FW_5005", "FW_5006",
                        "FW_5007"
                }
        )
        String errorCode,

        @Schema(
                description = "Detailed error message",
                example = "Token limit exceeded in the request",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String errorMessage,

        @Schema(
                description = "Additional error context as key-value pairs",
                example = "{\"maxTokens\": 4096, \"requestTokens\": 5120}",
                requiredMode = Schema.RequiredMode.NOT_REQUIRED
        )
        Map<String, Object> parameters
) {

    @Builder
    public ErrorResponse {
    }
}