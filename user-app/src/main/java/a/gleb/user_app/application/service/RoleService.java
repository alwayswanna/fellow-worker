package a.gleb.user_app.application.service;

import a.gleb.user_app.application.port.out.RoleRepositoryPort;
import a.gleb.user_app.domain.exception.RoleCodeAlreadyExistsException;
import a.gleb.user_app.domain.exception.RoleDisplayNameAlreadyExistsException;
import a.gleb.user_app.domain.exception.RoleNotFoundException;
import a.gleb.user_app.domain.model.PageQuery;
import a.gleb.user_app.domain.model.PageResult;
import a.gleb.user_app.domain.model.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepositoryPort roleRepositoryPort;

    public Role save(Role role) {
        return roleRepositoryPort.save(role);
    }

    @Transactional(readOnly = true)
    public PageResult<Role> findAll(PageQuery pageQuery) {
        return roleRepositoryPort.findAll(pageQuery);
    }

    @Transactional(readOnly = true)
    public List<Role> findAll() {
        return roleRepositoryPort.findAllSelectable();
    }

    @Transactional(readOnly = true)
    public Role findById(UUID id) {
        return roleRepositoryPort.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("role with `id=%s` not found".formatted(id)));
    }

    @Transactional
    public Role create(CreateRoleCommand command, String createdBy) {
        log.info("RoleService: creating role, [code={}]", command.code());

        if (roleRepositoryPort.existsByCode(command.code())) {
            throw new RoleCodeAlreadyExistsException("role with `code=%s` already exists".formatted(command.code()));
        }
        if (roleRepositoryPort.existsByDisplayName(command.displayName())) {
            throw new RoleDisplayNameAlreadyExistsException("role with `displayName=%s` already exists".formatted(command.displayName()));
        }

        var role = Role.builder()
                .code(command.code())
                .displayName(command.displayName())
                .selectable(command.selectable() == null || command.selectable())
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();

        var saved = roleRepositoryPort.save(role);
        log.info("RoleService: role created, [id={}, code={}]", saved.getId(), saved.getCode());

        return saved;
    }

    @Transactional
    public Role update(UUID id, UpdateRoleCommand command, String updatedBy) {
        log.info("RoleService: updating role, [id={}]", id);

        var role = roleRepositoryPort.findById(id)
                .orElseThrow(() -> new RoleNotFoundException("role with `id=%s` not found".formatted(id)));

        if (command.code() != null) role.setCode(command.code());
        if (command.displayName() != null) role.setDisplayName(command.displayName());
        if (command.selectable() != null) role.setSelectable(command.selectable());
        role.setUpdatedAt(LocalDateTime.now());
        role.setUpdatedBy(updatedBy);

        var saved = roleRepositoryPort.save(role);
        log.info("RoleService: role updated, [id={}]", saved.getId());

        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        log.info("RoleService: deleting role, [id={}]", id);

        if (!roleRepositoryPort.existsById(id)) {
            throw new RoleNotFoundException("role with `id=%s` not found".formatted(id));
        }
        roleRepositoryPort.deleteById(id);
        log.info("RoleService: role deleted, [id={}]", id);
    }
}
