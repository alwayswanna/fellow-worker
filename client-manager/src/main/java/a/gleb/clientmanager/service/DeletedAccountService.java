/*
 * Copyright (c) 3-3/25/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.service;

import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.dao.DeletedAccount;
import a.gleb.oauth2persistence.db.repository.DeletedAccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class DeletedAccountService {

    private final DeletedAccountRepository deletedAccountRepository;

    /**
     * The method saves account data when deleting which caused errors
     *
     * @param id {@link Account} id
     */
    public void safe(@NonNull UUID id) {
        deletedAccountRepository.save(
                DeletedAccount.builder()
                        .id(id)
                        .build()
        );
    }

    /**
     * Method find all deleted accounts.
     */
    public List<DeletedAccount> findAllDeletedAccounts() {
        return deletedAccountRepository.findAll();
    }

    /**
     * Method delete deleted account {@link DeletedAccount}
     */
    public void delete(@NonNull DeletedAccount deletedAccount) {
        deletedAccountRepository.delete(deletedAccount);
    }
}
