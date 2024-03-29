/*
 * Copyright (c) 07-3/30/23, 11:08 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.controller;

import a.gleb.apicommon.clientmanager.model.AccountRequestModel;
import a.gleb.apicommon.clientmanager.model.ApiResponseModel;
import a.gleb.apicommon.clientmanager.model.ChangePasswordModel;
import a.gleb.clientmanager.service.UserAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static a.gleb.clientmanager.configuration.OpenApiConfiguration.OAUTH2_SECURITY_SCHEMA;
import static a.gleb.clientmanager.controller.AccountController.CLIENT_MANAGER_CONTROLLER;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/account")
@Tag(name = CLIENT_MANAGER_CONTROLLER)
public class AccountController {

    public static final String CLIENT_MANAGER_CONTROLLER = "client.manager.controller";

    private final UserAccountService userAccountService;

    @Operation(
            summary = "Создать новую учетную запись.",
            tags = CLIENT_MANAGER_CONTROLLER
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "Bad request", responseCode = "400",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    )
            }
    )
    @PostMapping("/create")
    public ApiResponseModel create(@RequestBody AccountRequestModel requestModel) {
        return userAccountService.createAccount(requestModel);
    }

    @Operation(
            summary = "Изменить данные учетной записи.",
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(description = "Bad request", responseCode = "401"),
                    @ApiResponse(description = "Bad request", responseCode = "401",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)),
                    @ApiResponse(description = "Internal server error", responseCode = "500",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            }
    )
    @PutMapping("/edit")
    public ApiResponseModel edit(@RequestBody AccountRequestModel requestModel) {
        return userAccountService.editAccount(requestModel);
    }

    @Operation(
            summary = "Удалить данные учетной записи.",
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
    @DeleteMapping("/delete")
    public ApiResponseModel delete() {
        return userAccountService.deleteAccount();
    }

    @Operation(
            summary = "Получить данные аккаунта.",
            tags = CLIENT_MANAGER_CONTROLLER
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
    @GetMapping("/data")
    public ApiResponseModel getAccountInfo(@RequestParam @NotNull UUID userId) {
        return userAccountService.getAccountData(userId);
    }

    @Operation(
            summary = "Сменить пароль пользователя.",
            
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
    @PutMapping("/change-password")
    public ApiResponseModel changePassword(@RequestBody @Valid ChangePasswordModel changePasswordModel) {
        return userAccountService.changeUserPassword(changePasswordModel);
    }

    @Operation(
            summary = "Получить данные текущего аккаунта.",
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
    @GetMapping("/current")
    public ApiResponseModel getCurrentAccountData(){
        return userAccountService.getCurrentAccountData();
    }
}
