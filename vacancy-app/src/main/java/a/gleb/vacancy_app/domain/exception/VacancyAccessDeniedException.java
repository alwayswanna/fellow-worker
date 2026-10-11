package a.gleb.vacancy_app.domain.exception;

public class VacancyAccessDeniedException extends DomainException {

    public VacancyAccessDeniedException(String message) {
        super(ErrorCode.FORBIDDEN, message);
    }
}
