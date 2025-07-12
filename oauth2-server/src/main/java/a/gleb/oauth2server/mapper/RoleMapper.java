package a.gleb.oauth2server.mapper;

import a.gleb.oauth2server.db.entity.RoleEntity;
import a.gleb.oauth2server.model.Role.RoleRequest;
import a.gleb.oauth2server.model.Role.RoleResponse;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    /**
     * Request to entity mapping.
     * @param request data for create new role.
     * @return {@link RoleEntity} data to save.
     */
    public RoleEntity toEntity(RoleRequest request) {
        return RoleEntity.builder()
                .displayName(request.displayName())
                .roleName(request.roleName())
                .build();
    }

    /**
     * Map existing entity to response model.
     * @param entity data from database.
     * @return {@link RoleResponse} response model.
     */
    public RoleResponse toResponse(RoleEntity entity) {
        return new RoleResponse(entity.getId(), entity.getRoleName(), entity.getDisplayName(), entity.getLastUpdate());
    }

}
