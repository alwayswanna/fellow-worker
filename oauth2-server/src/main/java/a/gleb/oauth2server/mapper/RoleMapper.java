package a.gleb.oauth2server.mapper;

import a.gleb.oauth2server.db.entity.RoleEntity;
import a.gleb.oauth2server.model.Role.RoleRequest;
import a.gleb.oauth2server.model.Role.RoleResponse;
import org.springframework.stereotype.Component;

@Component
public class RoleMapper {

    public RoleEntity toEntity(RoleRequest request) {
        return RoleEntity.builder().build();
    }

    public RoleResponse toResponse(RoleEntity entity) {
        return null;
    }

}
