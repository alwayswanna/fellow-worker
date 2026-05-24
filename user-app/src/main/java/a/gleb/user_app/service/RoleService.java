package a.gleb.user_app.service;

import a.gleb.user_app.db.entity.RoleEntity;
import a.gleb.user_app.db.repository.RoleEntityRepository;
import a.gleb.user_app.exception.UserAppException;
import a.gleb.user_app.mapper.RoleMapper;
import a.gleb.user_app.model.RoleRequest;
import a.gleb.user_app.model.RoleResponse;
import a.gleb.user_app.model.RoleUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleEntityRepository roleEntityRepository;
    private final RoleMapper roleMapper;

    public RoleEntity save(RoleEntity entity) {
        return roleEntityRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public Page<RoleResponse> findAll(Pageable pageable) {
        return roleEntityRepository.findAll(pageable)
                .map(roleMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> findAll() {
        return roleEntityRepository.findAllBySelectableTrue()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RoleResponse findById(UUID id) {
        return roleEntityRepository.findById(id)
                .map(roleMapper::toResponse)
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "role with `id=%s` not found".formatted(id)));
    }

    @Transactional
    public RoleResponse create(RoleRequest request, String createdBy) {
        log.info("RoleService: creating role, [code={}]", request.code());

        if (roleEntityRepository.existsRoleEntityByCode(request.code())) {
            throw new UserAppException(CONFLICT, "role with `code=%s` already exists".formatted(request.code()));
        }
        if (roleEntityRepository.existsRoleEntityByDisplayName(request.displayName())) {
            throw new UserAppException(CONFLICT, "role with `displayName=%s` already exists".formatted(request.displayName()));
        }

        var entity = roleMapper.toEntity(request, createdBy);
        var saved = roleEntityRepository.save(entity);
        log.info("RoleService: role created, [id={}, code={}]", saved.getId(), saved.getCode());

        return roleMapper.toResponse(saved);
    }

    @Transactional
    public RoleResponse update(UUID id, RoleUpdateRequest request, String updatedBy) {
        log.info("RoleService: updating role, [id={}]", id);

        var entity = roleEntityRepository.findById(id)
                .orElseThrow(() -> new UserAppException(NOT_FOUND, "role with `id=%s` not found".formatted(id)));

        roleMapper.applyUpdate(entity, request, updatedBy);
        var saved = roleEntityRepository.save(entity);
        log.info("RoleService: role updated, [id={}]", saved.getId());

        return roleMapper.toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("RoleService: deleting role, [id={}]", id);

        if (!roleEntityRepository.existsById(id)) {
            throw new UserAppException(NOT_FOUND, "role with `id=%s` not found".formatted(id));
        }
        roleEntityRepository.deleteById(id);
        log.info("RoleService: role deleted, [id={}]", id);
    }
}
