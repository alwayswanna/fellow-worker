package a.gleb.vacancy_app.domain.exception;

public class AlreadyAppliedException extends DomainException {

    public AlreadyAppliedException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
