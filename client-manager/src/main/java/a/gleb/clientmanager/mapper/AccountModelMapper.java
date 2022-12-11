package a.gleb.clientmanager.mapper;

import a.gleb.clientmanager.model.AccountRequestModel;
import a.gleb.clientmanager.model.AccountType;
import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.dao.AccountRole;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountModelMapper {

    private final PasswordEncoder passwordEncoder;

    public Account mapToAccount(AccountRequestModel requestModel) {
        return Account.builder()
                .username(requestModel.getUsername())
                .email(requestModel.getEmail())
                .password(passwordEncoder.encode(requestModel.getPassword()))
                .firstName(requestModel.getFirstName())
                .middleName(requestModel.getMiddleName())
                .lastName(requestModel.getLastName())
                .role(AccountType.EMPLOYEE == requestModel.getAccountType() ?
                        AccountRole.COMPANY : AccountRole.EMPLOYEE
                )
                .enabled(true)
                .build();
    }
}
