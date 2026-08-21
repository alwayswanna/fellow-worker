package a.gleb.user_app.domain.exception;

public class RoleNotFoundException extends DomainException {

    public RoleNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
