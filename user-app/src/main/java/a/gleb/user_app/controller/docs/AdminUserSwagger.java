package a.gleb.user_app.controller.docs;

import a.gleb.fellow_worker.http.response.PageResponse;
import a.gleb.user_app.model.UserResponse;
import a.gleb.user_app.model.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;

import java.util.UUID;

import static a.gleb.user_app.config.UserAppConfiguration.OAUTH_SECURITY_SCHEME;

@Tag(name = "admin.user.controller")
@SecurityRequirement(name = OAUTH_SECURITY_SCHEME)
public interface AdminUserSwagger {

    @Operation(summary = "Get all users (paginated)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "Access denied"),
    })
    PageResponse<UserResponse> getAll(Pageable pageable);

    @Operation(summary = "Get user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    UserResponse getById(UUID id);

    @Operation(summary = "Update user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    UserResponse update(UUID id, UserUpdateRequest request, Authentication authentication);

    @Operation(summary = "Delete user by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    void delete(UUID id);
}
