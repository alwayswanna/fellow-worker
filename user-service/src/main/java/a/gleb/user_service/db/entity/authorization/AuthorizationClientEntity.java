package a.gleb.user_service.db.entity.authorization;

import a.gleb.user_service.db.entity.RoleEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "oauth2_registered_client")
public class AuthorizationClientEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "client_id", nullable = false)
    private String clientId;

    @Column(name = "client_id_issued_at", nullable = false)
    private LocalDateTime clientIdIssuedAt;

    @Column(name = "client_secret", nullable = false)
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private LocalDateTime clientSecretExpiresAt;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_authentication_methods", nullable = false)
    private String clientAuthenticationMethods;

    @Column(name = "authorization_grant_types", nullable = false)
    private String authorizationGrantTypes;

    @Column(name = "redirect_uris")
    private String redirectUris;

    @Column(name = "post_logout_redirect_uris")
    private String postLogoutRedirectUris;

    @Column(name = "scopes", nullable = false)
    private String scopes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "client_settings", nullable = false, columnDefinition = "jsonb")
    private String clientSettings;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "token_settings", nullable = false, columnDefinition = "jsonb")
    private String tokenSettings;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "registered_client_role",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<RoleEntity> roles = new HashSet<>();
}
