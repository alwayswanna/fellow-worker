package a.gleb.user_app.application.service;

import java.time.LocalDate;

public record RegisterUserCommand(
        String login,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String password,
        String roleCode
) {
}
