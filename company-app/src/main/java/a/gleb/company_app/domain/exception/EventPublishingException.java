package a.gleb.company_app.domain.exception;

public class EventPublishingException extends DomainException {

    public EventPublishingException(String message) {
        super(ErrorCode.INTERNAL, message);
    }
}
