package a.gleb.user_service.service;

import a.gleb.apicommon.api.user_service.role.Role.RoleFilterRequest;
import a.gleb.apicommon.api.user_service.role.Role.RoleRequest;
import a.gleb.apicommon.api.user_service.role.Role.RoleResponse;
import a.gleb.user_service.db.repository.RoleRepository;
import a.gleb.user_service.db.scpecification.RoleEntitySpecification;
import a.gleb.user_service.exception.BadRequestException;
import a.gleb.user_service.exception.NotFoundException;
import a.gleb.user_service.mapper.RoleMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static a.gleb.user_service.constant.OAuth2ServerConstants.MAX_ENTITIES_PER_PAGE;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleMapper roleMapper;
    private final RoleRepository roleRepository;

    /**
     * Method for create new role.
     *
     * @param request information about new role.
     * @return {@link RoleResponse} information about created role.
     */
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByRoleName(request.roleName())) {
            throw new BadRequestException(String.format("Role with name %s already exists", request.roleName()));
        }

        var entity = roleMapper.toEntity(request);
        var savedEntity = roleRepository.save(entity);

        log.info("Created new role, [role_name={}, id={}]", request.roleName(), savedEntity.getId());

        return roleMapper.toResponse(savedEntity);
    }

    /**
     * Method for load roles by pages.
     *
     * @param page number of page.
     * @return {@link List}<{@link RoleResponse}> array with roles.
     */
    public List<RoleResponse> roles(int page) {
        log.debug("Request to find all roles. [page={}]", page);

        return roleRepository.findAll(PageRequest.of(page, MAX_ENTITIES_PER_PAGE))
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    /**
     * Method for update existing role.
     *
     * @param request data for update existing role.
     * @param id      identifier of existing role.
     * @return {@link RoleResponse} response with updated data of role.
     */
    @Transactional
    public RoleResponse update(RoleRequest request, UUID id) {
        if (StringUtils.isEmpty(request.roleName()) && StringUtils.isEmpty(request.displayName())) {
            throw new BadRequestException("One of field should be filled.");
        }

        var entity = roleRepository.findById(id);

        if (entity.isEmpty()) {
            throw new BadRequestException("Role with current ID does not exists.");
        }

        var role = entity.get();

        if (StringUtils.isNotEmpty(request.roleName())) {
            if (roleRepository.existsByRoleName(request.roleName())) {
                throw new BadRequestException(String.format("Role with roleName=%s already exist.", request.roleName()));
            }

            role.setRoleName(request.roleName());
        }

        if (StringUtils.isNotEmpty(request.displayName())) {
            role.setDisplayName(request.displayName());
        }

        var updatedRole = roleRepository.save(role);

        return roleMapper.toResponse(updatedRole);
    }

    /**
     * Method for search roles by filter.
     *
     * @param request filter for search.
     * @return {@link List}<{@link RoleResponse}> array with roles.
     */
    public List<RoleResponse> findByFilter(RoleFilterRequest request) {
        log.debug("Request to find roles by filter");

        var entities = roleRepository.findAll(
                RoleEntitySpecification.buildRoleEntitySpecificationByFilter(request),
                Pageable.ofSize(MAX_ENTITIES_PER_PAGE)
        );

        if (entities.isEmpty()) {
            throw new NotFoundException("Roles not found by current filter.");
        }

        log.debug("Found by filter, [count={}]", entities.stream().count());
        return entities.stream()
                .map(roleMapper::toResponse)
                .toList();
    }
}
