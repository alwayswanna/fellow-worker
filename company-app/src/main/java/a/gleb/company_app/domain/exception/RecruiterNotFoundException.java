package a.gleb.company_app.domain.exception;

public class RecruiterNotFoundException extends DomainException {

    public RecruiterNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
