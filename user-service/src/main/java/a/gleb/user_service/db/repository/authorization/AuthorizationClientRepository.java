package a.gleb.user_service.db.repository.authorization;

import a.gleb.user_service.db.entity.authorization.AuthorizationClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthorizationClientRepository extends JpaRepository<AuthorizationClientEntity, UUID> {

    boolean existsByClientId(String clientId);

    Optional<AuthorizationClientEntity> findAuthorizationClientEntityByClientId(String clientId);
}
