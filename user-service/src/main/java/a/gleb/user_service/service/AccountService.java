/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.user_service.service;

import a.gleb.apicommon.api.user_service.account.Account.*;
import a.gleb.apicommon.event.EventType;
import a.gleb.apicommon.event.account.AccountEvent;
import a.gleb.user_service.db.entity.AccountEntity;
import a.gleb.user_service.db.entity.OutboxMessageEntity;
import a.gleb.user_service.db.repository.AccountRepository;
import a.gleb.user_service.db.repository.OutboxMessageRepository;
import a.gleb.user_service.db.repository.RoleRepository;
import a.gleb.user_service.exception.BadRequestException;
import a.gleb.user_service.exception.ConflictException;
import a.gleb.user_service.exception.NotFoundException;
import a.gleb.user_service.mapper.AccountMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static a.gleb.user_service.constant.OAuth2ServerConstants.DEFAULT_ROLE_CODE_ON_ACCOUNT_CREATED;
import static a.gleb.user_service.constant.OAuth2ServerConstants.MAX_ENTITIES_PER_PAGE;
import static a.gleb.user_service.db.scpecification.AccountEntitySpecification.buildAccountEntitySpecificationByFilter;

@Slf4j
@Service
@AllArgsConstructor
public class AccountService {

    private final ObjectMapper objectMapper;
    private final AccountMapper accountMapper;
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final OutboxMessageRepository outboxMessageRepository;

    public Optional<AccountEntity> findAccountByUsernameOrEmail(String username) {
        return accountRepository.findByUsernameOrEmailOrPhoneNumber(username, username, username);
    }

    /**
     * Method for register new account and save outbox message event.
     *
     * @param request account data
     * @return {@link AccountResponse} saved account information.
     */
    @Transactional
    public AccountResponse create(AccountRequest request) {
        log.debug("Request to register account : [username={}]", request.username());
        if (accountRepository.existsByUsername(request.username())) {
            throw new BadRequestException(String.format("Invalid username. Account with username %s already exists!", request.username()));
        }

        var defaultRole = roleRepository.findByRoleName(DEFAULT_ROLE_CODE_ON_ACCOUNT_CREATED)
                .orElseThrow(() -> new ConflictException("Default role does not exist."));

        var entity = accountMapper.toEntity(request);
        entity.setRole(defaultRole);
        var savedEntity = accountRepository.save(entity);

        var event = accountMapper.toEvent(entity, EventType.CREATE);
        saveOutboxMessage(event);

        log.info("Created new account, [username={}, id={}]", request.username(), savedEntity.getId());
        return accountMapper.toResponse(savedEntity);
    }

    /**
     * Method for load existing account, pageable.
     *
     * @param page page number.
     * @return {@link List}<{@link AccountResponse}> list of account on page.
     */
    public List<AccountResponse> all(int page) {
        log.debug("Request to find all accounts. [page={}]", page);

        return accountRepository.findAll(PageRequest.of(page, MAX_ENTITIES_PER_PAGE))
                .stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    /**
     * Method for search account by username.
     *
     * @param username account`s username
     * @return {@link AccountResponse} response with account data, or throw {@link NotFoundException}.
     */
    public AccountResponse findByUsername(String username) {
        log.debug("Request to find account by username : [username={}]", username);

        return accountRepository.findByUsername(username)
                .map(accountMapper::toResponse)
                .orElseThrow(() -> new NotFoundException(String.format("Account with username %s not found!", username)));
    }

    /**
     * Method for search account by filter, call JpaSpecification for search.
     *
     * @param request {@link AccountFilterRequest} data for filter.
     * @return {@link List}<{@link AccountResponse}> list of accounts.
     */
    public List<AccountResponse> findByFilter(AccountFilterRequest request) {
        log.debug("Request to find account by filter");

        var entities = accountRepository.findAll(
                buildAccountEntitySpecificationByFilter(request),
                Pageable.ofSize(MAX_ENTITIES_PER_PAGE)
        );

        if (entities.isEmpty()) {
            throw new NotFoundException("Account not found by current filter.");
        }

        log.debug("Found by filter, [count={}]", entities.stream().count());
        return entities.stream()
                .map(accountMapper::toResponse)
                .toList();
    }

    /**
     * Method for update existing account data.
     *
     * @param request new account data.
     * @param id      user ID
     * @return {@link AccountResponse} response with updated data, or throw exception if account with id does not exist.
     */
    @Transactional
    public AccountResponse update(@Valid AccountUpdateRequest request, UUID id) {
        log.info("Update account with id, [accountId={}]", id);
        var account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new NotFoundException("Account with ID does not exists.");
        }


        var accountEntity = account.get();
        if (!request.username().equals(accountEntity.getUsername())) {
            if (accountRepository.existsByUsername(request.username())) {
                log.warn("Attempt to change username on username which already exists.");
                throw new BadRequestException(String.format("Account with username %s already exists.", request.username()));
            }
            accountEntity.setUsername(request.username());
        }

        if (StringUtils.isNotEmpty(request.firstName()) && !request.firstName().equals(accountEntity.getFirstName())) {
            accountEntity.setFirstName(request.firstName());
        }

        if (StringUtils.isNotEmpty(request.lastName()) && !request.lastName().equals(accountEntity.getLastName())) {
            accountEntity.setLastName(request.lastName());
        }

        if (StringUtils.isNotEmpty(request.middleName()) && !request.middleName().equals(accountEntity.getMiddleName())) {
            accountEntity.setMiddleName(request.middleName());
        }

        if (StringUtils.isNotEmpty(request.email()) && !request.email().equals(accountEntity.getEmail())) {
            accountEntity.setEmail(request.email());
        }

        if (request.birthDate() != null && !request.birthDate().equals(accountEntity.getBirthDate())) {
            accountEntity.setBirthDate(request.birthDate());
        }

        var savedEntity = accountRepository.save(accountEntity);

        return accountMapper.toResponse(savedEntity);
    }

    /**
     * Remove account by ID.
     *
     * @param accountId identifier for delete account.
     */
    @Transactional
    public void delete(UUID accountId) throws JsonProcessingException {
        var accountEntity = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account with current ID does not exist."));

        accountRepository.delete(accountEntity);
        var event = accountMapper.toEvent(accountEntity, EventType.DELETE);
        saveOutboxMessage(event);
    }

    /**
     * Update current user password user`s password
     *
     * @param request data for change password
     */
    public void changePassword(AccountChangePasswordRequest request) {
        if (!request.confirmNewPassword().equals(request.newPassword())) {
            throw new BadRequestException("New password mismatch.");
        }
    }

    public void saveOutboxMessage(AccountEvent event) {
        try {
            outboxMessageRepository.save(
                    OutboxMessageEntity.builder()
                            .bindingName("account-event-out-0")
                            .created(LocalDateTime.now())
                            .message(objectMapper.writeValueAsString(event))
                            .build()
            );
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new ConflictException(e.getMessage());
        }
    }
}
