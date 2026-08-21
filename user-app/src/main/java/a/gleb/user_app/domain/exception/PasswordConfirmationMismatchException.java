package a.gleb.user_app.domain.exception;

public class PasswordConfirmationMismatchException extends DomainException {

    public PasswordConfirmationMismatchException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
