package a.gleb.user_app.application.service;

import java.time.LocalDate;

/**
 * Null fields are ignored (partial update).
 */
public record UpdateUserCommand(
        String firstName,
        String lastName,
        LocalDate birthDate
) {
}
