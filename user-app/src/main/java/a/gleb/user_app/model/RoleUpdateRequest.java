package a.gleb.user_app.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update a role. Null fields are ignored.")
public record RoleUpdateRequest(

        @Schema(description = "Unique role code", example = "MODERATOR")
        String code,

        @Schema(description = "Human-readable role name", example = "Moderator")
        String displayName,

        @Schema(description = "Whether this role is available for selection by users on the frontend.")
        Boolean selectable
) {
}
