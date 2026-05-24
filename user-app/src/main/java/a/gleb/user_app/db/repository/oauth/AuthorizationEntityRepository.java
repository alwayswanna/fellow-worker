package a.gleb.user_app.db.repository.oauth;

import a.gleb.user_app.db.entity.oauth.AuthorizationEntity;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AuthorizationEntityRepository extends JpaRepository<AuthorizationEntity, UUID> {

    @Modifying
    @Query(
            "DELETE FROM AuthorizationEntity authorizationEntity WHERE authorizationEntity.id = :id"
    )
    void deleteById(@Param("id") @NonNull UUID id);

    Optional<AuthorizationEntity> findAuthorizationEntityById(UUID id);

    Optional<AuthorizationEntity> findByState(@NonNull String state);

    Optional<AuthorizationEntity> findByAuthorizationCodeValue(@NonNull String authorizationCodeValue);

    Optional<AuthorizationEntity> findByAccessTokenValue(@NonNull String accessTokenValue);

    Optional<AuthorizationEntity> findByRefreshTokenValue(@NonNull String refreshTokenValue);

    Optional<AuthorizationEntity> findByOidcIdTokenValue(@NonNull String oidcIdTokenValue);

    Optional<AuthorizationEntity> findByUserCodeValue(@NonNull String userCodeValue);

    Optional<AuthorizationEntity> findByDeviceCodeValue(@NonNull String deviceCodeValue);

    @Query("""
            SELECT a FROM AuthorizationEntity a WHERE
                a.state = :token OR
                a.accessTokenValue = :token OR
                a.refreshTokenValue = :token OR
                a.oidcIdTokenValue = :token OR
                a.userCodeValue = :token OR
                a.deviceCodeValue = :token
            """
    )
    Optional<AuthorizationEntity> findByDifferenceTokenValue(@Param("token") String tokenValue);
}
