package a.gleb.oauth2server.db.entity.authorization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "oauth2_authorization")
public class AuthorizationEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "registered_client_id", nullable = false)
    private String registeredClientId;

    @Column(name = "principal_name", nullable = false)
    private String principalName;

    @Column(name = "authorization_grant_type", nullable = false)
    private String authorizationGrantType;

    @Column(name = "authorized_scopes")
    private String authorizedScopes;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "attributes", columnDefinition = "jsonb")
    private String attributes;

    @Column(name = "state")
    private String state;

    @Column(name = "authorization_code_value")
    private String authorizationCodeValue;

    @Column(name = "authorization_code_issued_at")
    private Instant authorizationCodeIssuedAt;

    @Column(name = "authorization_code_expires_at")
    private Instant authorizationCodeExpiresAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "authorization_code_metadata", columnDefinition = "jsonb")
    private String authorizationCodeMetadata;

    @Column(name = "access_token_value")
    private String accessTokenValue;

    @Column(name = "access_token_issued_at")
    private Instant accessTokenIssuedAt;

    @Column(name = "access_token_expires_at")
    private Instant accessTokenExpiresAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "access_token_metadata", columnDefinition = "jsonb")
    private String accessTokenMetadata;

    @Column(name = "access_token_type")
    private String accessTokenType;

    @Column(name = "access_token_scopes")
    private String accessTokenScopes;

    @Column(name = "oidc_id_token_value")
    private String oidcIdTokenValue;

    @Column(name = "oidc_id_token_issued_at")
    private Instant oidcIdTokenIssuedAt;

    @Column(name = "oidc_id_token_expires_at")
    private Instant oidcIdTokenExpiresAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "oidc_id_token_claims", columnDefinition = "jsonb")
    private String oidcIdTokenClaims;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "oidc_id_token_metadata", columnDefinition = "jsonb")
    private String oidcIdTokenMetadata;

    @Column(name = "refresh_token_value")
    private String refreshTokenValue;

    @Column(name = "refresh_token_issued_at")
    private Instant refreshTokenIssuedAt;

    @Column(name = "refresh_token_expires_at")
    private Instant refreshTokenExpiresAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "refresh_token_metadata", columnDefinition = "jsonb")
    private String refreshTokenMetadata;

    @Column(name = "user_code_value")
    private String userCodeValue;

    @Column(name = "user_code_issued_at")
    private Instant userCodeIssuedAt;

    @Column(name = "user_code_expires_at")
    private Instant userCodeExpiresAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "user_code_metadata", columnDefinition = "jsonb")
    private String userCodeMetadata;

    @Column(name = "device_code_value")
    private String deviceCodeValue;

    @Column(name = "device_code_issued_at")
    private Instant deviceCodeIssuedAt;

    @Column(name = "device_code_expires_at")
    private Instant deviceCodeExpiresAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "device_code_metadata", columnDefinition = "jsonb")
    private String deviceCodeMetadata;

    @Column(name = "date_create")
    private LocalDateTime dateCreate = LocalDateTime.now();
}
