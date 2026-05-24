package a.gleb.user_app.mapper;

import a.gleb.user_app.db.entity.RoleEntity;
import a.gleb.user_app.model.RoleRequest;
import a.gleb.user_app.model.RoleResponse;
import a.gleb.user_app.model.RoleUpdateRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class RoleMapper {

    public RoleEntity toEntity(RoleRequest request, String createdBy) {
        return RoleEntity.builder()
                .code(request.code())
                .displayName(request.displayName())
                .selectable(request.selectable() == null || request.selectable())
                .createdAt(LocalDateTime.now())
                .createdBy(createdBy)
                .build();
    }

    public void applyUpdate(RoleEntity entity, RoleUpdateRequest request, String updatedBy) {
        if (request.code() != null) entity.setCode(request.code());
        if (request.displayName() != null) entity.setDisplayName(request.displayName());
        if (request.selectable() != null) entity.setSelectable(request.selectable());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(updatedBy);
    }

    public RoleResponse toResponse(RoleEntity entity) {
        return RoleResponse.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .displayName(entity.getDisplayName())
                .selectable(entity.isSelectable())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
