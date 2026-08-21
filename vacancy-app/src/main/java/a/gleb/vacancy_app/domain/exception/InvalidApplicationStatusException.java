package a.gleb.vacancy_app.domain.exception;

public class InvalidApplicationStatusException extends DomainException {

    public InvalidApplicationStatusException(String message) {
        super(ErrorCode.BAD_REQUEST, message);
    }
}
