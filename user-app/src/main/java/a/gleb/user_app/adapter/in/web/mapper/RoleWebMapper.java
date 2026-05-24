package a.gleb.user_app.adapter.in.web.mapper;

import a.gleb.user_app.adapter.in.web.dto.RoleRequest;
import a.gleb.user_app.adapter.in.web.dto.RoleResponse;
import a.gleb.user_app.adapter.in.web.dto.RoleUpdateRequest;
import a.gleb.user_app.application.service.CreateRoleCommand;
import a.gleb.user_app.application.service.UpdateRoleCommand;
import a.gleb.user_app.domain.model.Role;
import org.springframework.stereotype.Component;

@Component
public class RoleWebMapper {

    public CreateRoleCommand toCommand(RoleRequest request) {
        return new CreateRoleCommand(request.code(), request.displayName(), request.selectable());
    }

    public UpdateRoleCommand toCommand(RoleUpdateRequest request) {
        return new UpdateRoleCommand(request.code(), request.displayName(), request.selectable());
    }

    public RoleResponse toResponse(Role role) {
        return RoleResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .displayName(role.getDisplayName())
                .selectable(role.isSelectable())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .build();
    }
}
