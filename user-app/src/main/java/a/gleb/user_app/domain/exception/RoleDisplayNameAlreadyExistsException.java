package a.gleb.user_app.domain.exception;

public class RoleDisplayNameAlreadyExistsException extends DomainException {

    public RoleDisplayNameAlreadyExistsException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
