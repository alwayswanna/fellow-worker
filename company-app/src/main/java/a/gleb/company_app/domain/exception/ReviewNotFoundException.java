package a.gleb.company_app.domain.exception;

public class ReviewNotFoundException extends DomainException {

    public ReviewNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
