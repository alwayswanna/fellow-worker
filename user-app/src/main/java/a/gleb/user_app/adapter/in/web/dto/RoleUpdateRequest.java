package a.gleb.user_app.adapter.in.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Request to update a role. Null fields are ignored.")
public record RoleUpdateRequest(

        @Pattern(regexp = ".*\\S.*", message = "must not be blank")
        @Schema(description = "Unique role code", example = "MODERATOR")
        String code,

        @Pattern(regexp = ".*\\S.*", message = "must not be blank")
        @Schema(description = "Human-readable role name", example = "Moderator")
        String displayName,

        @Schema(description = "Whether this role is available for selection by users on the frontend.")
        Boolean selectable
) {
}
