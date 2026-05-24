package a.gleb.user_app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Schema(description = "Request to register a new user")
public record UserRequest(

        @NotBlank
        @Schema(description = "Unique user login", example = "john_doe", requiredMode = Schema.RequiredMode.REQUIRED)
        String login,

        @NotBlank
        @Schema(description = "First name", example = "John", requiredMode = Schema.RequiredMode.REQUIRED)
        String firstName,

        @NotBlank
        @Schema(description = "Last name", example = "Doe", requiredMode = Schema.RequiredMode.REQUIRED)
        String lastName,

        @NotNull
        @Schema(description = "Date of birth", example = "1990-01-15", requiredMode = Schema.RequiredMode.REQUIRED)
        LocalDate birthDate,

        @NotBlank
        @Schema(description = "Password", example = "s3cr3t!", requiredMode = Schema.RequiredMode.REQUIRED)
        String password,

        @NotBlank
        @Schema(description = "Role code assigned to the user", requiredMode = Schema.RequiredMode.REQUIRED)
        String code
) {
}