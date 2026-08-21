package a.gleb.user_app.adapter.out.persistence.mapper;

import a.gleb.user_app.adapter.out.persistence.entity.UserEntity;
import a.gleb.user_app.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPersistenceMapper {

    private final RolePersistenceMapper rolePersistenceMapper;

    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .birthDate(entity.getBirthDate())
                .password(entity.getPassword())
                .role(rolePersistenceMapper.toDomain(entity.getRole()))
                .photoKey(entity.getPhotoKey())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .version(entity.getVersion())
                .build();
    }

    /**
     * Skips the (lazy) `role` association entirely - mirrors the pre-refactor `searchByQuery`
     * path, which is deliberately not fetched with an entity graph since none of its callers
     * ever read the role, so eagerly resolving it here would reintroduce an N+1.
     */
    public User toDomainShort(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        return User.builder()
                .id(entity.getId())
                .login(entity.getLogin())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .build();
    }

    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserEntity.builder()
                .id(user.getId())
                .login(user.getLogin())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthDate(user.getBirthDate())
                .password(user.getPassword())
                .role(rolePersistenceMapper.toEntity(user.getRole()))
                .photoKey(user.getPhotoKey())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .createdBy(user.getCreatedBy())
                .updatedBy(user.getUpdatedBy())
                .version(user.getVersion())
                .build();
    }
}
