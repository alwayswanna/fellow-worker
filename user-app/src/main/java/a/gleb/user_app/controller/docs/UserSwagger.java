package a.gleb.user_app.controller.docs;

import a.gleb.user_app.model.ChangePasswordRequest;
import a.gleb.user_app.model.UserResponse;
import a.gleb.user_app.model.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import java.time.LocalDate;
import java.util.UUID;

import static a.gleb.user_app.config.UserAppConfiguration.OAUTH_SECURITY_SCHEME;

@Tag(name = "user.controller")
public interface UserSwagger {

    @Operation(summary = "Register a new user (multipart/form-data)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Role not found"),
            @ApiResponse(responseCode = "409", description = "Login already taken"),
    })
    UserResponse register(
            @Parameter(description = "Unique user login", required = true) @NotBlank String login,
            @Parameter(description = "First name", required = true) @NotBlank String firstName,
            @Parameter(description = "Last name", required = true) @NotBlank String lastName,
            @Parameter(description = "Date of birth (YYYY-MM-DD)", required = true) @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate birthDate,
            @Parameter(description = "Password", required = true) @NotBlank String password,
            @Parameter(description = "Role code", required = true) @NotBlank String code,
            @Parameter(description = "Profile photo (optional)") MultipartFile photo);

    @Operation(summary = "Get current user profile", security = @SecurityRequirement(name = OAUTH_SECURITY_SCHEME))
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    UserResponse getMe(Authentication authentication);

    @Operation(summary = "Update current user profile", security = @SecurityRequirement(name = OAUTH_SECURITY_SCHEME))
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    UserResponse updateMe(Authentication authentication, UserUpdateRequest request);

    @Operation(summary = "Change password", security = @SecurityRequirement(name = OAUTH_SECURITY_SCHEME))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Password changed successfully"),
            @ApiResponse(responseCode = "400", description = "Old password incorrect or confirmation mismatch"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    void changePassword(Authentication authentication, ChangePasswordRequest request);

    @Operation(summary = "Upload profile photo", security = @SecurityRequirement(name = OAUTH_SECURITY_SCHEME))
    @ApiResponses({
            @ApiResponse(responseCode = "200", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found"),
    })
    UserResponse uploadPhoto(Authentication authentication, MultipartFile file);

    @Operation(summary = "Get profile photo by user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Photo bytes"),
            @ApiResponse(responseCode = "404", description = "User or photo not found"),
    })
    ResponseEntity<byte[]> getPhoto(UUID id);
}
