package a.gleb.company_app.domain.exception;

public class NotCompanyOwnerException extends DomainException {

    public NotCompanyOwnerException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
