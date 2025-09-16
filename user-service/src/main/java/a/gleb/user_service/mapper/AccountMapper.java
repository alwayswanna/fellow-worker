package a.gleb.user_service.mapper;

import a.gleb.apicommon.api.user_service.account.Account.AccountRequest;
import a.gleb.apicommon.api.user_service.account.Account.AccountResponse;
import a.gleb.apicommon.event.EventType;
import a.gleb.apicommon.event.account.AccountAdditionalInformation;
import a.gleb.apicommon.event.account.AccountEvent;
import a.gleb.user_service.db.entity.AccountEntity;
import a.gleb.user_service.db.entity.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

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
                .phoneNumber(account.phoneNumber())
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
                account.getUpdatedAt(),
                mapAuthorities(account.getRole())
        );
    }

    /**
     * Create event from account
     *
     * @param account domain object {@link AccountEntity}
     * @param type    operation type
     * @return event for sending to broker.
     */
    public AccountEvent toEvent(AccountEntity account, EventType type) {
        return AccountEvent.builder()
                .eventId(UUID.randomUUID())
                .type(type)
                .createdAt(LocalDateTime.now())
                .information(
                        AccountAdditionalInformation.builder()
                                .accountId(account.getId())
                                .roleIds(extractRoleIds(account.getRole()))
                                .username(account.getUsername())
                                .enabled(account.isEnabled())
                                .build()
                )
                .build();
    }

    /**
     * Method mapper, map account`s authorities for return it to API.
     */
    private List<String> mapAuthorities(RoleEntity authorities) {
        return authorities == null ?
                Collections.emptyList() :
                Collections.singletonList(authorities.getRoleName());
    }

    /**
     * Method extract role IDs
     *
     * @param role account roles.
     * @return emptySet() or Set<UUID> with account roles.
     */
    private static Set<UUID> extractRoleIds(RoleEntity role) {
        return role == null ?
                Collections.emptySet() :
                Set.of(role.getId());
    }
}
