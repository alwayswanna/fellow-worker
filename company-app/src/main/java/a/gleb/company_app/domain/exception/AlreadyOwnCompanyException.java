package a.gleb.company_app.domain.exception;

public class AlreadyOwnCompanyException extends DomainException {

    public AlreadyOwnCompanyException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
