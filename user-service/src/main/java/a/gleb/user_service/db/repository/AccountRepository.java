package a.gleb.user_service.db.repository;

import a.gleb.user_service.db.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID>, JpaSpecificationExecutor<AccountEntity> {

    Optional<AccountEntity> findByUsernameOrEmailOrPhoneNumber(String username, String email, String phoneNumber);

    boolean existsByUsername(String username);

    Optional<AccountEntity> findByUsername(String username);

    List<AccountEntity> findAllByIdIn(Collection<UUID> ids);
}
