package a.gleb.company_app.domain.exception;

public class AlreadyRecruiterElsewhereException extends DomainException {

    public AlreadyRecruiterElsewhereException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
