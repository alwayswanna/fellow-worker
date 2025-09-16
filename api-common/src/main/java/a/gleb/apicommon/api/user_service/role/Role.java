package a.gleb.apicommon.api.user_service.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Model for working with role`s API.
 */
public class Role {

    @Schema(description = "Request for create new role.")
    public record RoleRequest(
            @NotEmpty(message = "Role name is required")
            @Schema(description = "Name of role.")
            String roleName,
            @NotEmpty(message = "Display name is required")
            @Schema(description = "Display name of role.")
            String displayName
    ) {
    }

    @Schema(description = "Model for show information about existing role.")
    public record RoleResponse(
            @Schema(description = "Role`s ID")
            UUID id,
            @Schema(description = "Role`s name")
            String roleName,
            @Schema(description = "Role`s display name")
            String displayName,
            @Schema(description = "Timestamp of last update")
            LocalDateTime lastUpdate
    ) {
    }

    @Schema(description = "Filter for search roles")
    public record RoleFilterRequest(
            @Schema(description = "List of role IDs.")
            List<UUID> ids,
            @Schema(description = "Role display name.")
            String displayName,
            @Schema(description = "Role name.")
            String roleName
    ) {
    }
}