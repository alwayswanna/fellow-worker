package a.gleb.user_app.db.repository.oauth;

import a.gleb.user_app.db.entity.oauth.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientEntityRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findClientEntityByClientId(String clientId);

    boolean existsClientEntityByClientId(String clientId);
}
