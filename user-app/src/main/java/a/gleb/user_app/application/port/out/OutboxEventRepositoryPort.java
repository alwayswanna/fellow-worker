package a.gleb.user_app.application.port.out;

import a.gleb.user_app.domain.model.OutboxEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutboxEventRepositoryPort {

    void save(OutboxEvent event);

    Optional<OutboxEvent> findById(UUID id);

    List<OutboxEvent> findPending(int batchSize);
}
