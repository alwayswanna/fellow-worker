package a.gleb.vacancy_app.domain.exception;

public class VacancyNotFoundException extends DomainException {

    public VacancyNotFoundException(String message) {
        super(ErrorCode.NOT_FOUND, message);
    }
}
