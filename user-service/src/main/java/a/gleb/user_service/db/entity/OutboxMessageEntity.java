package a.gleb.user_service.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "outbox_message")
public class OutboxMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "binding_name", nullable = false)
    private String bindingName;

    @Column(name = "is_sent", nullable = false)
    private boolean sent;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Version
    @Column(name = "version", nullable = false)
    private long version;
}
