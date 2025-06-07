package a.gleb.oauth2server.db.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static jakarta.persistence.GenerationType.AUTO;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "account")
public class AccountEntity {

    @Id
    @GeneratedValue(strategy = AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "middle_name", nullable = false)
    private String middleName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = false;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "last_update", nullable = false)
    private LocalDateTime lastUpdate;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "account_role",
            joinColumns = @JoinColumn(name = "account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();
}
