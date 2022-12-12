package a.gleb.clientmanager.controller;

import a.gleb.clientmanager.model.AccountRequestModel;
import a.gleb.clientmanager.model.ApiResponseModel;
import a.gleb.clientmanager.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import static a.gleb.clientmanager.configuration.OpenApiConfiguration.OAUTH2_SECURITY_SCHEMA;
import static a.gleb.clientmanager.controller.AccountController.CLIENT_MANAGER_CONTROLLER;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/account")
@Tag(name = CLIENT_MANAGER_CONTROLLER)
public class AccountController {

    public static final String CLIENT_MANAGER_CONTROLLER = "client.manager.controller";

    private final AccountService accountService;

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
    public ApiResponseModel create(@RequestBody @Valid AccountRequestModel requestModel) {
        return accountService.createAccount(requestModel);
    }

    @Operation(
            summary = "Изменить данные учетной записи.",
            tags = CLIENT_MANAGER_CONTROLLER,
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "Bad request", responseCode = "401",
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
    @PutMapping("/edit")
    public ApiResponseModel edit(@RequestBody @Valid AccountRequestModel requestModel) {
        return accountService.editAccount(requestModel);
    }

    @Operation(
            summary = "Изменить данные учетной записи.",
            tags = CLIENT_MANAGER_CONTROLLER,
            security = @SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            description = "OK", responseCode = "200",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ApiResponseModel.class))
                    ),
                    @ApiResponse(
                            description = "Bad request", responseCode = "401",
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
    @DeleteMapping("/delete")
    public ApiResponseModel delete() {
        return accountService.deleteAccount();
    }
}
