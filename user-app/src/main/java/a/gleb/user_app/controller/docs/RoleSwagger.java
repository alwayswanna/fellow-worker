package a.gleb.user_app.controller.docs;

import a.gleb.user_app.model.RoleResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

import static a.gleb.user_app.config.UserAppConfiguration.OAUTH_SECURITY_SCHEME;

@Tag(name = "role.controller")
@SecurityRequirement(name = OAUTH_SECURITY_SCHEME)
public interface RoleSwagger {

    @Operation(summary = "Get all roles")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
    })
    List<RoleResponse> getAll();

    @Operation(summary = "Get role by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
    })
    RoleResponse getById(UUID id);
}
