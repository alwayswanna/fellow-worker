package a.gleb.user_app.domain.exception;

public class InvalidPhotoException extends DomainException {

    public InvalidPhotoException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
