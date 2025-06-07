package a.gleb.oauth2server.controller;

import a.gleb.oauth2server.model.Role.RoleFilterRequest;
import a.gleb.oauth2server.model.Role.RoleRequest;
import a.gleb.oauth2server.model.Role.RoleResponse;
import a.gleb.oauth2server.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static a.gleb.oauth2server.constant.OAuth2ServerConstants.OAUTH2_SERVER_DEFINITION;

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
