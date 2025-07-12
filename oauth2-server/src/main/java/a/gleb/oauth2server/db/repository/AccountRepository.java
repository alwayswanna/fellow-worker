package a.gleb.oauth2server.db.repository;

import a.gleb.oauth2server.db.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID>, JpaSpecificationExecutor<AccountEntity> {

    Optional<AccountEntity> findByUsernameOrEmail(String username, String email);

    boolean existsByUsername(String username);

    Optional<AccountEntity> findByUsername(String username);

    List<AccountEntity> findAllByIdIn(Collection<UUID> ids);
}
