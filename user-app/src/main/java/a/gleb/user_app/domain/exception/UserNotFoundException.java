package a.gleb.user_app.domain.exception;

public class UserNotFoundException extends DomainException {

    public UserNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
