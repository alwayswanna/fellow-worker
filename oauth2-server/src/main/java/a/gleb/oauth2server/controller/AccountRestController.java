package a.gleb.oauth2server.controller;

import a.gleb.oauth2server.model.Account;
import a.gleb.oauth2server.model.Account.*;
import a.gleb.oauth2server.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static a.gleb.oauth2server.constant.OAuth2ServerConstants.OAUTH2_SERVER_DEFINITION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
@Tag(name = "account.rest.controller", description = "Controller for account APIs.")
public class AccountRestController {

    private final AccountService accountService;

    @Operation(
            summary = "Register new account.",
            description = "Method for create new account.",
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @ResponseBody
    @PostMapping("/create")
    public AccountResponse create(@RequestBody @Valid AccountRequest request) {
        return accountService.create(request);
    }

    @Operation(
            summary = "Load user accounts.",
            description = "Method for load pages with accounts.",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @ResponseBody
    @GetMapping("/all")
    public List<AccountResponse> all(@RequestParam(required = false, defaultValue = "0") int page) {
        return accountService.all(page);
    }

    @Operation(
            summary = "Find account by username",
            description = "Find existing account by username",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @ResponseBody
    @GetMapping
    public AccountResponse findByUsername(@RequestParam(required = true) String username) {
        return accountService.findByUsername(username);
    }

    @Operation(
            summary = "Find account by filter.",
            description = "Method for search account by filter.",
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
    public List<AccountResponse> findByFilter(@RequestBody AccountFilterRequest request) {
        return accountService.findByFilter(request);
    }

    @Operation(
            summary = "Update account data.",
            description = "Method for update existing account.",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @ResponseBody
    @PutMapping
    public AccountResponse update(@RequestBody @Valid AccountUpdateRequest request, @RequestParam(required = true) UUID id) {
        return accountService.update(request, id);
    }

    @Operation(
            summary = "Delete account with selected ID.",
            description = "Remove account & linked account entities in another systems.",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @DeleteMapping
    public void delete(@RequestParam ("id") UUID id) throws JsonProcessingException {
        accountService.delete(id);
    }

    @Operation(
            summary = "Method for changed password.",
            description = "Password can be changed by admin or user.",
            security = @SecurityRequirement(name = OAUTH2_SERVER_DEFINITION),
            responses = {
                    @ApiResponse(description = "OK", responseCode = "200"),
                    @ApiResponse(description = "Bad request", responseCode = "400"),
                    @ApiResponse(description = "Forbidden", responseCode = "403"),
                    @ApiResponse(description = "Not found", responseCode = "404"),
                    @ApiResponse(description = "Internal server error", responseCode = "500")
            }
    )
    @PatchMapping("/password-change")
    public void changedPassword(@RequestBody @Valid AccountChangePasswordRequest request) {
        accountService.changePassword(request);
    }
}
