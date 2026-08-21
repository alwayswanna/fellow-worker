package a.gleb.user_app.domain.exception;

public class RoleCodeAlreadyExistsException extends DomainException {

    public RoleCodeAlreadyExistsException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
