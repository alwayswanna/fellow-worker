package a.gleb.user_app.application.service;

/**
 * Null fields are ignored (partial update).
 */
public record UpdateRoleCommand(
        String code,
        String displayName,
        Boolean selectable
) {
}
