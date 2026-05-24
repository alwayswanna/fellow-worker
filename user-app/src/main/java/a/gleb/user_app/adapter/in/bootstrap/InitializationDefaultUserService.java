package a.gleb.user_app.adapter.in.bootstrap;

import a.gleb.user_app.application.port.out.PasswordHasherPort;
import a.gleb.user_app.application.port.out.RoleRepositoryPort;
import a.gleb.user_app.application.port.out.UserRepositoryPort;
import a.gleb.user_app.application.service.RoleService;
import a.gleb.user_app.application.service.UserService;
import a.gleb.user_app.config.properties.UserAppConfigurationProperties;
import a.gleb.user_app.domain.model.Role;
import a.gleb.user_app.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
    private final RoleRepositoryPort roleRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordHasherPort passwordHasherPort;
    private final UserAppConfigurationProperties properties;

    @Override
    public void run(@NonNull ApplicationArguments args) {
        var role = properties.role();
        if (role != null) {
            var roleValue = roleRepositoryPort.findByCode(role.roleCode())
                    .orElse(null);

            if (roleValue == null) {
                if (roleRepositoryPort.existsByDisplayName(role.roleName())) {
                    log.info("InitializationDefaultUserService: role with display name already exists, skipping creation, [role_name={}]", role.roleName());
                    return;
                }

                roleValue = Role.builder()
                        .code(role.roleCode())
                        .displayName(role.roleName())
                        .createdBy("system")
                        .createdAt(LocalDateTime.now())
                        .build();

                roleValue = roleService.save(roleValue);
                log.info("InitializationDefaultUserService: default role was created, [role_code={}]", role.roleCode());
            } else {
                log.info("InitializationDefaultUserService: default role already exists, [role_code={}]", role.roleCode());
            }

            var admin = properties.admin();
            if (admin != null) {
                if (userRepositoryPort.existsByLogin(admin.username())) {
                    log.info("InitializationDefaultUserService: default user already exists, [username={}]", admin.username());
                    return;
                }

                var adminUser = User.builder()
                        .login(admin.username())
                        .role(roleValue)
                        .firstName("System")
                        .lastName("Administrator")
                        .birthDate(LocalDate.now())
                        .password(passwordHasherPort.encode(admin.password()))
                        .createdAt(LocalDateTime.now())
                        .createdBy("system")
                        .build();

                userService.save(adminUser);
                log.info("InitializationDefaultUserService: default user was created, [username={}]", admin.username());
            }
        }
    }
}
