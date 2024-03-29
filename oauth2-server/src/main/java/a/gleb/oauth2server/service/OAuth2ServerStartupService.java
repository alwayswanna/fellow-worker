/*
 * Copyright (c) 07-3/31/23, 9:26 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2server.service;

import a.gleb.oauth2persistence.db.dao.AccountRole;
import a.gleb.oauth2server.configuration.properties.OAuth2ServerProperties;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

import static a.gleb.oauth2persistence.db.dao.Account.builder;

@Slf4j
@Service
@AllArgsConstructor
public class OAuth2ServerStartupService implements ApplicationRunner {

    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final OAuth2ServerProperties oAuth2ServerProperties;


    @Override
    public void run(ApplicationArguments args) {
        OAuth2ServerProperties.DefaultUser defaultUser = oAuth2ServerProperties.getDefaultUser();

        if (accountService.findAccountByUsernameOrEmail(defaultUser.getUsername()).isEmpty()) {
            log.info("{}, create default user start ... ", getClass().getSimpleName());
            var defaultUserAccount = builder()
                    .username(defaultUser.getUsername())
                    .password(passwordEncoder.encode(defaultUser.getPassword()))
                    .email(defaultUser.getEmail())
                    .firstName("Администратор")
                    .middleName("(Administrator)")
                    .birthData(LocalDate.of(1998, 2, 17))
                    .enabled(true)
                    .role(AccountRole.ADMIN)
                    .build();
            accountService.save(defaultUserAccount);
            log.info("{}, create default user end ... ", getClass().getSimpleName());
        }
    }
}
