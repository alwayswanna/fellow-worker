package a.gleb.user_app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to create a new role")
public record RoleRequest(

        @NotBlank
        @Schema(description = "Unique role code", example = "MODERATOR", requiredMode = Schema.RequiredMode.REQUIRED)
        String code,

        @NotBlank
        @Schema(description = "Human-readable role name", example = "Moderator", requiredMode = Schema.RequiredMode.REQUIRED)
        String displayName,

        @Schema(description = "Whether this role is available for selection by users on the frontend. Defaults to true.", example = "true")
        Boolean selectable
) {
}
