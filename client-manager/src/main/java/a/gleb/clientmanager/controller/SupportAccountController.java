package a.gleb.clientmanager.controller;

import a.gleb.apicommon.clientmanager.model.ApiResponseModel;
import a.gleb.clientmanager.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static a.gleb.clientmanager.configuration.OpenApiConfiguration.OAUTH2_SECURITY_SCHEMA;
import static a.gleb.clientmanager.controller.SupportAccountController.CLIENT_SUPPORT_CONTROLLER;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/support")
@Tag(name = CLIENT_SUPPORT_CONTROLLER)
public class SupportAccountController {

    public static final String CLIENT_SUPPORT_CONTROLLER = "client.support.controller";

    private final SupportService supportService;

    @Operation(
            summary = "Отключить аккаунт.",
            tags = CLIENT_SUPPORT_CONTROLLER,
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "401",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @GetMapping("/disable-account")
    public ApiResponseModel getAccountInfo(@RequestParam @NotNull UUID userId) {
        return supportService.disable(userId);
    }

    @Operation(
            summary = "Получить все аккаунты.",
            tags = CLIENT_SUPPORT_CONTROLLER,
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "401",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @GetMapping("/accounts")
    public ApiResponseModel getAllAccounts() {
        return supportService.getAllAccounts();
    }

    @Operation(
            summary = "Получить все аккаунты.",
            tags = CLIENT_SUPPORT_CONTROLLER,
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "401",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @GetMapping("/account-by-username")
    public ApiResponseModel getAllAccounts(@RequestParam @NotEmpty String username) {
        return supportService.getAccountByUserName(username);
    }
}
