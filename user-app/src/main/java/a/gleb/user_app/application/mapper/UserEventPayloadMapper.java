package a.gleb.user_app.application.mapper;

import a.gleb.user_app.domain.model.User;
import a.gleb.fellow_worker.kafka.event.UserEventPayload;
import a.gleb.fellow_worker.kafka.event.UserEventType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class UserEventPayloadMapper {

    public UserEventPayload toPayload(User user, UserEventType eventType) {
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
