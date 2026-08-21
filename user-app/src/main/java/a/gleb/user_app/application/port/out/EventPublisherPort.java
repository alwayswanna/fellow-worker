package a.gleb.user_app.application.port.out;

import a.gleb.user_app.domain.model.OutboxEvent;

public interface EventPublisherPort {

    void publish(OutboxEvent event);
}
