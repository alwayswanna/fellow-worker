package a.gleb.user_app.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@Schema(description = "Request to update user account. Null fields are ignored.")
public record UserUpdateRequest(

        @Pattern(regexp = ".*\\S.*", message = "must not be blank")
        @Schema(description = "First name", example = "John")
        String firstName,

        @Pattern(regexp = ".*\\S.*", message = "must not be blank")
        @Schema(description = "Last name", example = "Doe")
        String lastName,

        @Schema(description = "Date of birth", example = "1990-01-15")
        LocalDate birthDate
) {
}
