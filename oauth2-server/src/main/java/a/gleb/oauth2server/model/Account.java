package a.gleb.oauth2server.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Model for working with user`s API.
 */
public class Account {

    @Schema(description = "Model for show information about existing account.")
    public record AccountResponse(
            @Schema(description = "Account`s ID")
            UUID id,

            @Schema(description = "Username for login")
            String username,

            @Schema(description = "User`s first name")
            String firstName,

            @Schema(description = "User`s last name")
            String lastName,

            @Schema(description = "User`s middle name")
            String middleName,

            @Schema(description = "User`s email")
            String email,

            @Schema(description = "User`s date of birth")
            LocalDate birthDate,

            @Schema(description = "Account`s status")
            boolean enabled,

            @Schema(description = "Date&Time of last changes.")
            LocalDateTime lastUpdate,

            @Schema(description = "Account`s permissions.")
            List<String> authorities
    ) {
    }

    @Schema(description = "Request to register new account.")
    public record AccountRequest(

            @Schema(description = "username for login")
            @NotEmpty(message = "username can`t be empty")
            String username,

            @Schema(description = "password for login")
            @NotEmpty(message = "password can`t be empty")
            @Size(min = 8, message = "Min size of password is 8 chars.")
            String password,

            @Schema(description = "First name")
            @NotEmpty(message = "first name required")
            String firstName,

            @Schema(description = "Last name")
            String lastName,

            @Schema(description = "Middle name")
            @NotEmpty(message = "middle name required")
            String middleName,

            @Schema(description = "Account`s email.")
            @Email(message = "required actual email")
            @NotEmpty(message = "email can`t be empty")
            String email,

            @Schema(description = "Account`s status, active or not.")
            boolean enabled,

            @Schema(description = "Date of birth")
            @NotNull(message = "birth date required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate birthDate
    ) {
    }

    @Schema(description = "Request for update existed account.")
    public record AccountUpdateRequest(
            @Schema(description = "username for login")
            String username,
            @Schema(description = "First name")
            String firstName,
            @Schema(description = "Last name")
            String lastName,
            @Schema(description = "Middle name")
            String middleName,
             @Schema(description = "Account`s email.")
            String email,
            @Schema(description = "Date of birth")
            LocalDate birthDate
    ) {}

    @Schema(description = "Filter for search account")
    public record AccountFilterRequest(

            @Schema(description = "Find accounts where IDs IN.")
            List<UUID> ids,

            @Schema(description = "Find account by username.")
            String username,

            @Schema(description = "Find account`s with authorities.")
            List<String> authorities,

            @Schema(description = "Find account by email.")
            String email,

            @Schema(description = "Find account with status.")
            Boolean enabled,

            @Schema(description = "Find account by middle name.")
            String middleName,

            @Schema(description = "Find account by first name.")
            String firstName,

            @Schema(description = "Find account by last name.")
            String lastName
    ) {
    }

    @Schema(description = "Request on change password for existing account")
    public record AccountChangePasswordRequest (

            @Schema(description = "Current password value.")
            String currentPassword,

            @NotEmpty
            @Size(min = 8, message = "Min size of password is 8 chars.")
            @Schema(description = "New password value.")
            String newPassword,

            @NotEmpty
            @Size(min = 8, message = "Min size of password is 8 chars.")
            @Schema(description = "New password confirm.")
            String confirmNewPassword
    ) {}
}
