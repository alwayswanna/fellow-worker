package a.gleb.fellow_worker.common;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for transferring validation error information.
 * <p>
 * Used to provide a detailed description of validation errors for request fields.
 *
 * @param field Name of the field where the validation error occurred
 * @param error Validation error message for the specified field
 */
@Schema(description = "Validation error details for a specific field")
public record ValidationErrorDto(
        @Schema(
                description = "Name of the field that failed validation",
                example = "email",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String field,

        @Schema(
                description = "Validation error message",
                example = "Must be a valid email address",
                requiredMode = Schema.RequiredMode.REQUIRED
        )
        String error
) {
}
