package a.gleb.user_app.application.service;

public record CreateRoleCommand(
        String code,
        String displayName,
        Boolean selectable
) {
}
