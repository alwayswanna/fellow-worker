package a.gleb.user_app.db.repository;

import a.gleb.user_app.db.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleEntityRepository extends JpaRepository<RoleEntity, UUID> {

    boolean existsRoleEntityByCode(String code);

    boolean existsRoleEntityByDisplayName(String displayName);

    Optional<RoleEntity> findRoleEntityByCode(String code);

    List<RoleEntity> findAllBySelectableTrue();
}