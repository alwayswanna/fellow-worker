package a.gleb.user_app.db.repository;

import a.gleb.user_app.db.entity.OutboxEventEntity;
import a.gleb.fellow_worker.kafka.event.OutboxEventStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {

    @Query("SELECT e FROM OutboxEventEntity e WHERE e.status = :status ORDER BY e.createdAt ASC")
    List<OutboxEventEntity> findPendingEvents(@Param("status") OutboxEventStatus status, Pageable pageable);
}
