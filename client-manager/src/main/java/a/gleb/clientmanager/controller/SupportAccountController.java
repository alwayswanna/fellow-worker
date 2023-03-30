/*
 * Copyright (c) 07-3/31/23, 9:26 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.controller;

import a.gleb.apicommon.clientmanager.model.AddRedirectUriModel;
import a.gleb.apicommon.clientmanager.model.ApiResponseModel;
import a.gleb.apicommon.clientmanager.model.ChangeClientCredentialsModel;
import a.gleb.clientmanager.service.SupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

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
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @GetMapping("/disable-account")
    public ApiResponseModel disableAccount(@RequestParam @NotNull UUID userId) {
        return supportService.disable(userId);
    }

    @Operation(
            summary = "Получить все аккаунты.",
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @GetMapping("/accounts")
    public ApiResponseModel getAccountByUsername() {
        return supportService.getAllAccounts();
    }

    @Operation(
            summary = "Получить аккаунт по username.",
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @GetMapping("/account-by-username")
    public ApiResponseModel getAccountByUsername(@RequestParam @NotEmpty String username) {
        return supportService.getAccountByUserName(username);
    }

    @Operation(
            summary = "Удалить аккаунт и все связанные сущности.",
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @DeleteMapping("/remove-account-by-id")
    public ApiResponseModel removeAccountById(@RequestParam UUID accountId){
        return supportService.removeUserAndUserEntitiesById(accountId);
    }

    @Operation(
            summary = "Сменить секрет клиента.",
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PutMapping("/change-client-secret")
    public ApiResponseModel changeClientSecret(@RequestBody @Valid ChangeClientCredentialsModel request){
        return supportService.updateRegisteredClientSecret(request);
    }

    @Operation(
            summary = "Добавить редирект пути для клиента.",
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(description = "Unauthorized", responseCode = "401",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Forbidden", responseCode = "403",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PutMapping("/add-redirect-uris")
    public ApiResponseModel addRedirectUriToClient(@RequestBody @Valid AddRedirectUriModel request){
        return supportService.addNewRedirectUrisToClient(request);
    }
}
