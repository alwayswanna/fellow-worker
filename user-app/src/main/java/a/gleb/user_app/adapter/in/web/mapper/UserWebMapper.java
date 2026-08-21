package a.gleb.user_app.adapter.in.web.mapper;

import a.gleb.user_app.adapter.in.web.dto.ChangePasswordRequest;
import a.gleb.user_app.adapter.in.web.dto.RoleResponse;
import a.gleb.user_app.adapter.in.web.dto.UserResponse;
import a.gleb.user_app.adapter.in.web.dto.UserShortResponse;
import a.gleb.user_app.adapter.in.web.dto.UserUpdateRequest;
import a.gleb.user_app.application.service.ChangePasswordCommand;
import a.gleb.user_app.application.service.RegisterUserCommand;
import a.gleb.user_app.application.service.UpdateUserCommand;
import a.gleb.user_app.domain.model.Role;
import a.gleb.user_app.domain.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class UserWebMapper {

    public RegisterUserCommand toRegisterCommand(
            String login, String firstName, String lastName, LocalDate birthDate, String password, String roleCode
    ) {
        return new RegisterUserCommand(login, firstName, lastName, birthDate, password, roleCode);
    }

    public UpdateUserCommand toCommand(UserUpdateRequest request) {
        return new UpdateUserCommand(request.firstName(), request.lastName(), request.birthDate());
    }

    public ChangePasswordCommand toCommand(ChangePasswordRequest request) {
        return new ChangePasswordCommand(request.oldPassword(), request.newPassword(), request.confirmPassword());
    }

    /**
     * Mirrors the pre-refactor `UserMapper.toRoleResponse`, which never set `selectable` on the
     * role nested inside a user response (unlike the standalone role endpoints) - preserved as-is
     * to avoid changing the `GET/PUT .../users/**` response shape.
     */
    private RoleResponse toRoleResponse(Role role) {
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

    public UserShortResponse toShortResponse(User user) {
        return new UserShortResponse(user.getId(), user.getLogin(), user.getFirstName(), user.getLastName());
    }

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .login(user.getLogin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .role(toRoleResponse(user.getRole()))
                .photoUrl(user.getPhotoKey() != null ? "/api/v1/users/" + user.getId() + "/photo" : null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .build();
    }
}
