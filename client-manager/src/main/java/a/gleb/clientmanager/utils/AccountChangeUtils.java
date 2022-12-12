package a.gleb.clientmanager.utils;

import a.gleb.clientmanager.model.AccountRequestModel;
import a.gleb.clientmanager.model.AccountType;
import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.dao.AccountRole;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public final class AccountChangeUtils {

    private AccountChangeUtils() {}

    public static void changeAccountData(Account account, AccountRequestModel accountRequestModel) {
        if (!isEmpty(accountRequestModel.getUsername())
                && !Objects.equals(accountRequestModel.getUsername(), account.getUsername())) {
            account.setUsername(accountRequestModel.getUsername());
        }

        if (!isEmpty(accountRequestModel.getPassword())) {
            account.setPassword(accountRequestModel.getPassword());
        }

        if (!isEmpty(accountRequestModel.getEmail()) && !Objects.equals(accountRequestModel.getEmail(), account.getEmail())) {
            account.setEmail(accountRequestModel.getEmail());
        }

        if (!isEmpty(accountRequestModel.getFirstName()) &&
                !Objects.equals(accountRequestModel.getFirstName(), account.getFirstName())) {
            account.setFirstName(accountRequestModel.getFirstName());
        }

        if (!isEmpty(accountRequestModel.getMiddleName()) &&
                !Objects.equals(accountRequestModel.getMiddleName(), account.getMiddleName())) {
            account.setMiddleName(accountRequestModel.getMiddleName());
        }

        if (!isEmpty(accountRequestModel.getLastName()) &&
                !Objects.equals(accountRequestModel.getLastName(), account.getLastName())) {
            account.setLastName(accountRequestModel.getLastName());
        }

        if (accountRequestModel.getAccountType() != null) {
            account.setRole(accountRequestModel.getAccountType() == AccountType.EMPLOYEE ?
                    AccountRole.EMPLOYEE : AccountRole.COMPANY);
        }

        account.setEnabled(true);
    }

}
