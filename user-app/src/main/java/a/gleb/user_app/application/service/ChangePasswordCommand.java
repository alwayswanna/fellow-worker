package a.gleb.user_app.application.service;

public record ChangePasswordCommand(
        String oldPassword,
        String newPassword,
        String confirmPassword
) {
}
