package a.gleb.user_service.db.repository.authorization;

import a.gleb.user_service.db.entity.authorization.AuthorizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface AuthorizationRepository extends JpaRepository<AuthorizationEntity, UUID> {

    void removeById(UUID id);

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
