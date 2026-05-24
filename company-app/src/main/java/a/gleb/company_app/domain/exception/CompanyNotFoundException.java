package a.gleb.company_app.domain.exception;

public class CompanyNotFoundException extends DomainException {

    public CompanyNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
