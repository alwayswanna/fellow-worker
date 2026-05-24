package a.gleb.user_app.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Schema(description = "Role data")
public record RoleResponse(

        @Schema(description = "Role identifier")
        UUID id,

        @Schema(description = "Unique role code", example = "ADMIN")
        String code,

        @Schema(description = "Human-readable role name", example = "Administrator")
        String displayName,

        @Schema(description = "Whether this role is available for selection by users on the frontend.")
        boolean selectable,

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
