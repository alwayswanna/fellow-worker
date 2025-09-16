package a.gleb.user_service.db.repository;

import a.gleb.user_service.db.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface RoleRepository extends JpaRepository<RoleEntity, UUID>, JpaSpecificationExecutor<RoleEntity> {

    boolean existsByRoleName(String roleName);

    Optional<RoleEntity> findByRoleName(String roleName);
}
