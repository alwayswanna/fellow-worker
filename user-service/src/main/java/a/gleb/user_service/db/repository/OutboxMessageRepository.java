package a.gleb.user_service.db.repository;

import a.gleb.user_service.db.entity.OutboxMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxMessageRepository extends JpaRepository<OutboxMessageEntity, UUID> {

    List<OutboxMessageEntity> findAllBySent(boolean isSent);
}
