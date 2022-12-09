package a.gleb.oauth2server.service;


import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.repository.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    public Optional<Account> findAccountByUsernameOrEmail(String username) {
        return accountRepository.findAccountByUsernameOrEmail(username, username);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }
}
