package a.gleb.user_app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to change user password.")
public record ChangePasswordRequest(

        @NotBlank(message = "Old password is required")
        @Schema(description = "Current password", example = "0ldP@ssw0rd!")
        String oldPassword,

        @NotBlank(message = "New password is required")
        @Schema(description = "New password", example = "N3wP@ssw0rd!")
        String newPassword,

        @NotBlank(message = "Password confirmation is required")
        @Schema(description = "New password confirmation", example = "N3wP@ssw0rd!")
        String confirmPassword
) {
}
