package a.gleb.user_service.mapper;

import a.gleb.apicommon.api.user_service.role.Role.RoleRequest;
import a.gleb.apicommon.api.user_service.role.Role.RoleResponse;
import a.gleb.user_service.db.entity.RoleEntity;
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
        return new RoleResponse(entity.getId(), entity.getRoleName(), entity.getDisplayName(), entity.getUpdatedAt());
    }

}
