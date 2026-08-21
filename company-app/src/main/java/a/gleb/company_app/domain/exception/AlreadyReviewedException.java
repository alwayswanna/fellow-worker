package a.gleb.company_app.domain.exception;

public class AlreadyReviewedException extends DomainException {

    public AlreadyReviewedException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
