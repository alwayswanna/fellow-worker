package a.gleb.oauth2server.db.entity;

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
@Table(name = "outbox_messsage")
public class OutboxMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "sent", nullable = false)
    private boolean sent;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;
}
