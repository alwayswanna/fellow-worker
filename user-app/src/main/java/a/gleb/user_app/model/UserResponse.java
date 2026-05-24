package a.gleb.user_app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Registered user data")
public record UserResponse(

        @Schema(description = "User identifier")
        UUID id,

        @Schema(description = "Unique user login", example = "john_doe")
        String login,

        @Schema(description = "First name", example = "John")
        String firstName,

        @Schema(description = "Last name", example = "Doe")
        String lastName,

        @Schema(description = "Date of birth", example = "1990-01-15")
        LocalDate birthDate,

        @Schema(description = "Role assigned to the user")
        RoleResponse role,

        @Schema(description = "URL of the user's profile photo", nullable = true)
        String photoUrl,

        @Schema(description = "Timestamp when the record was created")
        LocalDateTime createdAt,

        @Schema(description = "Timestamp when the record was last updated")
        LocalDateTime updatedAt,

        @Schema(description = "Login of the user who created this record")
        String createdBy,

        @Schema(description = "Login of the user who last updated this record")
        String updatedBy
) {
}