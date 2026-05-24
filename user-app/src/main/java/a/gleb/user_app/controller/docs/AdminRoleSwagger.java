package a.gleb.user_app.controller.docs;

import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.user_app.model.RoleRequest;
import a.gleb.user_app.model.RoleResponse;
import a.gleb.user_app.model.RoleUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static a.gleb.user_app.config.UserAppConfiguration.OAUTH_SECURITY_SCHEME;

@Tag(name = "admin.role.controller")
@SecurityRequirement(name = OAUTH_SECURITY_SCHEME)
public interface AdminRoleSwagger {

    @Operation(summary = "Get all roles (paginated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "Access denied"),
    })
    PageResponse<RoleResponse> getAll(Pageable pageable);

    @Operation(summary = "Get role by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
    })
    RoleResponse getById(UUID id);

    @Operation(summary = "Create a new role")
    @ApiResponses({
            @ApiResponse(responseCode = "201", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "409", description = "Role already exists"),
    })
    RoleResponse create(RoleRequest request, Authentication authentication);

    @Operation(summary = "Update role by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
    })
    RoleResponse update(UUID id, RoleUpdateRequest request, Authentication authentication);

    @Operation(summary = "Delete role by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Role deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
    })
    void delete(UUID id);
}
