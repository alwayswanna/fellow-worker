package a.gleb.apicommon.api.user_service.account;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;
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
            @NotEmpty(message = "Username cannot be empty")
            String username,
            @Schema(description = "password for login")
            @NotEmpty(message = "Password cannot be empty")
            @Size(min = 8, message = "Password must be at least 8 characters long")
            String password,
            @Schema(description = "First name")
            @NotEmpty(message = "First name is required")
            String firstName,
            @Schema(description = "Last name")
            String lastName,
            @Schema(description = "Middle name")
            @NotEmpty(message = "Middle name is required")
            String middleName,
            @Schema(description = "Account`s email.")
            @Email(message = "Please provide a valid email address")
            @NotEmpty(message = "Email is required")
            String email,
            @Schema(description = "Account`s status, active or not.")
            boolean enabled,
            @Schema(description = "Date of birth")
            @NotNull(message = "Date of birth is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate birthDate,
            @Schema(description = "Phone number")
            @Pattern(regexp = "^\\d+$", message = "Phone number must contain only digits")
            @Length(max = 15, message = "Phone number cannot exceed 15 digits")
            @NotEmpty(message = "Phone number is required")
            String phoneNumber
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
            @Email(message = "Please provide a valid email address")
            String email,
            @Schema(description = "Date of birth")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
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
            @NotEmpty(message = "Current password is required")
            String currentPassword,
            @NotEmpty(message = "New password is required")
            @Size(min = 8, message = "New password must be at least 8 characters long")
            @Schema(description = "New password value.")
            String newPassword,
            @NotEmpty(message = "Password confirmation is required")
            @Size(min = 8, message = "Confirmed password must be at least 8 characters long")
            @Schema(description = "New password confirm.")
            String confirmNewPassword
    ) {}
}