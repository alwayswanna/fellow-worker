package a.gleb.company_app.application.port.out;

import a.gleb.company_app.domain.model.OutboxEvent;

public interface EventPublisherPort {

    void publish(OutboxEvent event);
}
