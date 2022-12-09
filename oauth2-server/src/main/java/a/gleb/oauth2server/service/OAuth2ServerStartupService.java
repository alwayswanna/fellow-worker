package a.gleb.oauth2server.service;

import a.gleb.oauth2persistence.db.dao.AccountRole;
import a.gleb.oauth2server.configuration.properties.OAuth2ServerProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static a.gleb.oauth2persistence.db.dao.Account.builder;

@Slf4j
@Service
@AllArgsConstructor
public class OAuth2ServerStartupService implements ApplicationRunner {

    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final OAuth2ServerProperties oAuth2ServerProperties;


    @Override
    public void run(ApplicationArguments args) {
        log.info("{}, create default user start ... ", getClass().getSimpleName());
        OAuth2ServerProperties.DefaultUser defaultUser = oAuth2ServerProperties.getDefaultUser();

        if (!accountService.findAccountByUsernameOrEmail(defaultUser.getUsername()).isPresent()) {
            var defaultUserAccount = builder()
                    .username(defaultUser.getUsername())
                    .password(passwordEncoder.encode(defaultUser.getPassword()))
                    .email(defaultUser.getEmail())
                    .firstName("Администратор")
                    .middleName("(Administrator)")
                    .enabled(true)
                    .role(AccountRole.ADMIN)
                    .build();
            accountService.save(defaultUserAccount);
        }

        log.info("{}, create default user end ... ", getClass().getSimpleName());
    }
}
