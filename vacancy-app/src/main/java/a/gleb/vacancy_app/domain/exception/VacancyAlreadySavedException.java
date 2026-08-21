package a.gleb.vacancy_app.domain.exception;

public class VacancyAlreadySavedException extends DomainException {

    public VacancyAlreadySavedException(String message) {
        super(ErrorCode.CONFLICT, message);
    }
}
