package a.gleb.oauth2server.service;

import a.gleb.oauth2server.constant.OAuth2ServerConstants;
import a.gleb.oauth2server.db.repository.RoleRepository;
import a.gleb.oauth2server.db.scpecification.RoleEntitySpecification;
import a.gleb.oauth2server.exception.BadRequestException;
import a.gleb.oauth2server.exception.NotFoundException;
import a.gleb.oauth2server.mapper.RoleMapper;
import a.gleb.oauth2server.model.Role.RoleFilterRequest;
import a.gleb.oauth2server.model.Role.RoleRequest;
import a.gleb.oauth2server.model.Role.RoleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static a.gleb.oauth2server.constant.OAuth2ServerConstants.MAX_ENTITIES_PER_PAGE;

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
