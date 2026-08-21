package a.gleb.vacancy_app.domain.exception;

public class ApplicationNotFoundException extends DomainException {

    public ApplicationNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
