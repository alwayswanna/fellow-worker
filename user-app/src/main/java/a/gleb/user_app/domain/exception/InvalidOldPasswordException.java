package a.gleb.user_app.domain.exception;

public class InvalidOldPasswordException extends DomainException {

    public InvalidOldPasswordException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
