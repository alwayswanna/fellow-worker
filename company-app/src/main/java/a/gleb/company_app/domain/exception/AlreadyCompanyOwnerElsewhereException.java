package a.gleb.company_app.domain.exception;

public class AlreadyCompanyOwnerElsewhereException extends DomainException {

    public AlreadyCompanyOwnerElsewhereException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
