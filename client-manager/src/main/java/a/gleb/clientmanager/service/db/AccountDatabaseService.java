/*
 * Copyright (c) 3-3/30/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.service.db;

import a.gleb.clientmanager.exception.InvalidUserDataException;
import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service wrap repository layer.
 */
@Service
@AllArgsConstructor
public class AccountDatabaseService {

    private final AccountRepository accountRepository;

    /**
     * Method find account by id {@link Account#getId()}
     *
     * @param userId account id
     * @return {@link Account} or null
     */
    public Account findAccountById(@NonNull UUID userId) {
        return accountRepository
                .findAccountById(userId).
                orElseThrow(() -> new InvalidUserDataException(
                        HttpStatus.BAD_REQUEST,
                        "Неверно введены данные."
                ));
    }

    /**
     * Method save account
     *
     * @param account to save
     */
    public void saveAccount(@NonNull Account account) {
        accountRepository.save(account);
    }

    /**
     * Method delete account by id {@link Account#getId()}
     *
     * @param id for delete account
     */
    public void deleteAccountById(@NonNull UUID id) {
        accountRepository.deleteById(id);
    }

    /**
     * Method find account by username {@link Account#getUsername()} or email {@link Account#getEmail()}
     *
     * @param username for search account
     * @param email    for search account
     * @return null or {@link Account}
     */
    @Nullable
    public Account findAccountByUsernameOrEmail(@NonNull String username, @NonNull String email) {
        return accountRepository.findAccountByUsernameOrEmail(username, email).orElse(null);
    }

    /**
     * Method find all accounts.
     * @return {@link List} with accounts.
     */
    public List<Account> findAllAccounts(){
        return accountRepository.findAll();
    }
}
