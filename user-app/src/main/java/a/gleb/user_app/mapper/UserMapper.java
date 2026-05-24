package a.gleb.user_app.mapper;


import a.gleb.user_app.db.entity.RoleEntity;
import a.gleb.user_app.db.entity.UserEntity;
import a.gleb.user_app.model.RoleResponse;
import a.gleb.user_app.model.UserRequest;
import a.gleb.user_app.model.UserResponse;
import a.gleb.user_app.model.UserShortResponse;
import a.gleb.user_app.model.UserUpdateRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class UserMapper {

    public UserEntity toEntity(UserRequest request, RoleEntity roleEntity) {
        return UserEntity.builder()
                .login(request.login())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .password(request.password())
                .role(roleEntity)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private RoleResponse toRoleResponse(RoleEntity role) {
        return RoleResponse.builder()
                .id(role.getId())
                .code(role.getCode())
                .displayName(role.getDisplayName())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .createdBy(role.getCreatedBy())
                .updatedBy(role.getUpdatedBy())
                .build();
    }

    public void applyUpdate(UserEntity entity, UserUpdateRequest request, String updatedBy) {
        if (request.firstName() != null) entity.setFirstName(request.firstName());
        if (request.lastName() != null) entity.setLastName(request.lastName());
        if (request.birthDate() != null) entity.setBirthDate(request.birthDate());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(updatedBy);
    }

    public UserShortResponse toShortResponse(UserEntity entity) {
        return new UserShortResponse(entity.getId(), entity.getLogin(), entity.getFirstName(), entity.getLastName());
    }

    public UserResponse toResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .birthDate(entity.getBirthDate())
                .role(toRoleResponse(entity.getRole()))
                .photoUrl(entity.getPhotoUrl() != null ? "/api/v1/users/" + entity.getId() + "/photo" : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}