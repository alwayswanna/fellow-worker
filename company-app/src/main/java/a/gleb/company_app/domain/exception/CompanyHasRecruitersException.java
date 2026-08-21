package a.gleb.company_app.domain.exception;

public class CompanyHasRecruitersException extends DomainException {

    public CompanyHasRecruitersException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
