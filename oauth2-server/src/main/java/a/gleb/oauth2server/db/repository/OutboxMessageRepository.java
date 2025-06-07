package a.gleb.oauth2server.db.repository;

import a.gleb.oauth2server.db.entity.OutboxMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessageEntity, UUID> {

    List<OutboxMessageEntity> findAllBySent(boolean isSent);
}
