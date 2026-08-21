package a.gleb.vacancy_app.domain.exception;

public class ApplicationAlreadyWithdrawnException extends DomainException {

    public ApplicationAlreadyWithdrawnException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
