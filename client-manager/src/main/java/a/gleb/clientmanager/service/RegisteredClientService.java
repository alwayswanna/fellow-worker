/*
 * Copyright (c) 3-3/29/23, 8:31 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static a.gleb.clientmanager.constants.RegisteredClientsSqlConstant.*;
import static java.lang.String.format;

@Slf4j
@Service
@AllArgsConstructor
public class RegisteredClientService {

    private static final String COMMA_DELIMITER = ",";

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void changeUserSecretForClient(@NonNull String clientId, @NonNull String newSecret) {
        log.info("Start update client secret. [client_id={}, client_secret={}]", clientId, newSecret);

        var encodedSecret = passwordEncoder.encode(newSecret);
        var result = jdbcTemplate.update(SQL_SECRET_UPDATE_QUERY, encodedSecret, clientId);
        if (result != 1) {
            throw new IllegalStateException(format("Updated lines more or less then 1, result = %s", result));
        }

        log.info("End update client secret. [client_id={}]", clientId);
    }

    @Transactional
    public void addedNewRedirectUrisForClient(@NonNull String clientId, List<String> redirectUris) {
        log.info("Start update client redirect uris. [client_id={}, redirect_uris={}]", clientId, redirectUris);

        var redirectUrisResult = jdbcTemplate.query(
                SQL_SELECT_REDIRECT_URIS_QUERY,
                new Object[]{clientId},
                new int[]{1},
                (resultSet, row) -> resultSet.getString(SQL_REDIRECT_URI_COLUMN_NAME)
        );

        String newRedirectUris;

        if (!CollectionUtils.isEmpty(redirectUrisResult)) {
            newRedirectUris = Stream.concat(
                            redirectUrisResult.stream().flatMap(s -> Stream.of(s.split(COMMA_DELIMITER))),
                            redirectUris.stream()
                    )
                    .collect(Collectors.joining(COMMA_DELIMITER));
        } else {
            newRedirectUris = String.join(COMMA_DELIMITER, redirectUrisResult);
        }

        var result = jdbcTemplate.update(SQL_UPDATE_REDIRECT_URIS_QUERY, newRedirectUris, clientId);
        if (result != 1) {
            throw new IllegalStateException(format("Updated lines more or less then 1, result = %s", result));
        }

        log.info("End update redirect uris. [client_id={}, redirect_uris={}]", clientId, newRedirectUris);
    }
}
