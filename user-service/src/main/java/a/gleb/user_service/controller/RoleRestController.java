package a.gleb.user_service.controller;

import a.gleb.apicommon.api.user_service.role.Role.RoleFilterRequest;
import a.gleb.apicommon.api.user_service.role.Role.RoleRequest;
import a.gleb.apicommon.api.user_service.role.Role.RoleResponse;
import a.gleb.user_service.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static a.gleb.user_service.constant.OAuth2ServerConstants.OAUTH2_SERVER_DEFINITION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/role")
@Tag(name = "role.rest.controller", description = "Controller for role APIs.")
public class RoleRestController {

    private final RoleService roleService;

    @Operation(
            summary = "Create new role.",
            description = "Method for crate new role.",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @ResponseBody
    @PostMapping("/create")
    public RoleResponse create(@RequestBody @Valid RoleRequest request) {
        return roleService.create(request);
    }

    @Operation(
            summary = "Update existed role.",
            description = "Method for updated existing role.",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @ResponseBody
    @PutMapping
    public RoleResponse update(@RequestBody RoleRequest request, @RequestParam UUID id) {
        return roleService.update(request, id);
    }

    @Operation(
            summary = "Get list of roles.",
            description = "Method for fetch existing roles.",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @ResponseBody
    @GetMapping("/all")
    public List<RoleResponse> roles(@RequestParam(required = true) int page) {
        return roleService.roles(page);
    }

    @Operation(
            summary = "Find roles by filter.",
            description = "Method for search roles by filter.",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @ResponseBody
    @PostMapping
    public List<RoleResponse> findByFilter(@RequestBody RoleFilterRequest request) {
        return roleService.findByFilter(request);
    }
}
