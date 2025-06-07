package a.gleb.oauth2server.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "role")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;

    @Column(name = "display_name", nullable = false, unique = true)
    private String displayName;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;
}
