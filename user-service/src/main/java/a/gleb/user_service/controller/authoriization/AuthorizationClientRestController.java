package a.gleb.user_service.controller.authoriization;

import a.gleb.apicommon.api.user_service.registered_client.AuthorizationClient.AuthorizationClientRequest;
import a.gleb.apicommon.api.user_service.registered_client.AuthorizationClient.AuthorizationClientResponse;
import a.gleb.user_service.service.authorization.AuthorizationClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static a.gleb.user_service.constant.OAuth2ServerConstants.OAUTH2_SERVER_DEFINITION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/registered-client")
@Tag(name = "authorization.client.rest.controller", description = "Controller for registered client APIs.")
public class AuthorizationClientRestController {

    private final AuthorizationClientService authorizationClientService;

    @Operation(
            summary = "Load all clients",
            description = "Method for load all authorization clients",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @ResponseBody
    @GetMapping("/all")
    public List<AuthorizationClientResponse> all(@RequestParam(required = true) int page) {
        return authorizationClientService.all(page);
    }

    @Operation(
            summary = "Create new authorization client",
            description = "Method for create authorization client",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Internal server error", responseCode = "500"),
            }
    )
    @ResponseBody
    @PostMapping("/create")
    public AuthorizationClientResponse create(@RequestBody @Valid AuthorizationClientRequest request) {
        return authorizationClientService.create(request);
    }

}
