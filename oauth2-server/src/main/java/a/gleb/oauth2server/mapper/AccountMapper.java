package a.gleb.oauth2server.mapper;

import a.gleb.oauth2server.db.entity.AccountEntity;
import a.gleb.oauth2server.db.entity.RoleEntity;
import a.gleb.oauth2server.model.Account.AccountRequest;
import a.gleb.oauth2server.model.Account.AccountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AccountMapper {

    private final PasswordEncoder passwordEncoder;

    /**
     * Method mapper, map request to entity.
     */
    public AccountEntity toEntity(AccountRequest account) {
        return AccountEntity.builder()
                .username(account.username())
                .password(passwordEncoder.encode(account.password()))
                .firstName(account.firstName())
                .lastName(account.lastName())
                .middleName(account.middleName())
                .email(account.email())
                .birthDate(account.birthDate())
                .enabled(account.enabled())
                .build();
    }

    /**
     * Method mapper, map entity to API response.
     */
    public AccountResponse toResponse(AccountEntity account) {
        return new AccountResponse(
                account.getId(),
                account.getUsername(),
                account.getFirstName(),
                account.getLastName(),
                account.getMiddleName(),
                account.getEmail(),
                account.getBirthDate(),
                account.isEnabled(),
                account.getLastUpdate(),
                mapAuthorities(account.getRoles())
        );
    }

    /**
     * Method mapper, map account`s authorities for return it to API.
     */
    private List<String> mapAuthorities(Collection<RoleEntity> authorities) {
        return authorities.stream()
                .map(RoleEntity::getRoleName)
                .toList();
    }
}
