package a.gleb.user_app.application.port.out;

import a.gleb.user_app.domain.model.PageQuery;
import a.gleb.user_app.domain.model.PageResult;
import a.gleb.user_app.domain.model.Role;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepositoryPort {

    boolean existsByCode(String code);

    boolean existsByDisplayName(String displayName);

    Optional<Role> findByCode(String code);

    List<Role> findAllSelectable();

    Role save(Role role);

    Optional<Role> findById(UUID id);

    boolean existsById(UUID id);

    void deleteById(UUID id);

    PageResult<Role> findAll(PageQuery pageQuery);
}
