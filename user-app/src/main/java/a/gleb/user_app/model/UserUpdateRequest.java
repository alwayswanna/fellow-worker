package a.gleb.user_app.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "Request to update user account. Null fields are ignored.")
public record UserUpdateRequest(

        @Schema(description = "First name", example = "John")
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        String lastName,

        @Schema(description = "Date of birth", example = "1990-01-15")
        LocalDate birthDate
) {
}