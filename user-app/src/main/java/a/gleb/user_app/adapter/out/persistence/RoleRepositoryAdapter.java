package a.gleb.user_app.adapter.out.persistence;

import a.gleb.user_app.adapter.out.persistence.mapper.RolePersistenceMapper;
import a.gleb.user_app.adapter.out.persistence.repository.RoleEntityRepository;
import a.gleb.user_app.application.port.out.RoleRepositoryPort;
import a.gleb.user_app.domain.model.PageQuery;
import a.gleb.user_app.domain.model.PageResult;
import a.gleb.user_app.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepositoryPort {

    private final RoleEntityRepository roleEntityRepository;
    private final RolePersistenceMapper rolePersistenceMapper;

    @Override
    public boolean existsByCode(String code) {
        return roleEntityRepository.existsRoleEntityByCode(code);
    }

    @Override
    public boolean existsByDisplayName(String displayName) {
        return roleEntityRepository.existsRoleEntityByDisplayName(displayName);
    }

    @Override
    public Optional<Role> findByCode(String code) {
        return roleEntityRepository.findRoleEntityByCode(code).map(rolePersistenceMapper::toDomain);
    }

    @Override
    public List<Role> findAllSelectable() {
        return roleEntityRepository.findAllBySelectableTrue().stream()
                .map(rolePersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Role save(Role role) {
        var saved = roleEntityRepository.save(rolePersistenceMapper.toEntity(role));
        return rolePersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<Role> findById(UUID id) {
        return roleEntityRepository.findById(id).map(rolePersistenceMapper::toDomain);
    }

    @Override
    public boolean existsById(UUID id) {
        return roleEntityRepository.existsById(id);
    }

    @Override
    public void deleteById(UUID id) {
        roleEntityRepository.deleteById(id);
    }

    @Override
    public PageResult<Role> findAll(PageQuery pageQuery) {
        var page = roleEntityRepository.findAll(PersistencePageSupport.toPageable(pageQuery));
        return PersistencePageSupport.toPageResult(page, rolePersistenceMapper::toDomain);
    }
}
