package a.gleb.oauth2server.model;

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
