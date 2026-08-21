package a.gleb.vacancy_app.domain.exception;

public class VacancyNotSavedException extends DomainException {

    public VacancyNotSavedException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
