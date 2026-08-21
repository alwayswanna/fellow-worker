package a.gleb.user_app.domain.exception;

public class LoginAlreadyExistsException extends DomainException {

    public LoginAlreadyExistsException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
