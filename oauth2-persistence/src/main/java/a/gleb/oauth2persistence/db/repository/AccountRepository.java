/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.oauth2persistence.db.repository;

import a.gleb.oauth2persistence.db.dao.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {

    Optional<Account> findAccountByUsernameOrEmail(String username, String email);

    Optional<Account> findAccountById(UUID userId);
}
