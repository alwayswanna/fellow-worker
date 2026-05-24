package a.gleb.user_app.service.init;

import a.gleb.user_app.config.properties.UserAppConfigurationProperties;
import a.gleb.user_app.db.entity.RoleEntity;
import a.gleb.user_app.db.entity.UserEntity;
import a.gleb.user_app.service.RoleService;
import a.gleb.user_app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.init.default-user", havingValue = "true")
public class InitializationDefaultUserService implements ApplicationRunner {

    private final UserService userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserAppConfigurationProperties properties;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        var role = properties.role();
        if (role != null) {
            var entity = RoleEntity.builder()
                    .code(role.roleCode())
                    .displayName(role.roleName())
                    .createdBy("system")
                    .createdAt(LocalDateTime.now())
                    .build();

            entity = roleService.save(entity);
            var admin = properties.admin();
            log.info("InitializationDefaultUserService: default role was created, [role_code={}]", role.roleCode());

            if (admin != null) {
                var adminEntity = UserEntity.builder()
                        .login(admin.username())
                        .role(entity)
                        .firstName("System")
                        .lastName("Administrator")
                        .birthDate(LocalDate.now())
                        .password(passwordEncoder.encode(admin.password()))
                        .createdAt(LocalDateTime.now())
                        .createdBy("system")
                        .build();

                userService.save(adminEntity);
                log.info("InitializationDefaultUserService: default user was created, [username={}]", admin.username());
            }
        }
    }
}
