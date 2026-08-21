package a.gleb.company_app.domain.exception;

public class OwnerCannotBeRecruiterException extends DomainException {

    public OwnerCannotBeRecruiterException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
