package a.gleb.resume_app.domain.exception;

public class ResumeNotFoundException extends DomainException {

    public ResumeNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
