package a.gleb.user_app.mapper;

import a.gleb.user_app.db.entity.UserEntity;
import a.gleb.fellow_worker.kafka.event.UserEventPayload;
import a.gleb.fellow_worker.kafka.event.UserEventType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserEventMapper {

    public UserEventPayload toPayload(UserEntity user, UserEventType eventType) {
        return new UserEventPayload(
                UUID.randomUUID(),
                eventType.name(),
                LocalDateTime.now(),
                user.getId(),
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.getBirthDate(),
                user.getRole().getCode(),
                user.getRole().getDisplayName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
