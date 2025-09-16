package a.gleb.user_service.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxMessageDeleteAccount {

    private UUID accountId;

    private LocalDateTime timestamp;
}
