package a.gleb.oauth2server.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class TokenService {

    private static final String USER_ID_CLAIM = "user_id";
    private static final String EMAIL_CLAIM = "email";
    private static final String USERNAME_CLAIM = "username";
    private static final String FIRSTNAME_CLAIM = "first_name";
    private static final String MIDDLE_NAME_CLAIM = "middle_name";

    private final AccountService accountService;

    public Map<String, String> enrichJwtToken(String username) {
        log.info("Enrich JWT for user: {}", username);
        var accountByUsername = accountService.findAccountByUsernameOrEmail(username);
        if (accountByUsername.isPresent()) {
            var account = accountByUsername.get();
            return Map.of(
                    USER_ID_CLAIM, account.getId().toString(),
                    EMAIL_CLAIM, account.getEmail(),
                    USERNAME_CLAIM, account.getUsername(),
                    FIRSTNAME_CLAIM, account.getFirstName(),
                    MIDDLE_NAME_CLAIM, account.getMiddleName()
            );
        }
        return Map.of();
    }
}
