package a.gleb.vacancy_app.domain.exception;

public class ResumeRequiredException extends DomainException {

    public ResumeRequiredException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}