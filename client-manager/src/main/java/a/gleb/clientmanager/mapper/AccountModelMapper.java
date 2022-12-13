package a.gleb.clientmanager.mapper;

import a.gleb.clientmanager.model.AccountDataModel;
import a.gleb.clientmanager.model.AccountRequestModel;
import a.gleb.clientmanager.model.AccountType;
import a.gleb.clientmanager.model.ApiResponseModel;
import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.dao.AccountRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AccountModelMapper {

    /**
     * Convert {@link AccountRequestModel} to {@link Account}
     *
     * @param requestModel body from request
     * @return {@link Account}
     */
    public Account mapToAccount(AccountRequestModel requestModel) {
        return Account.builder()
                .username(requestModel.getUsername())
                .email(requestModel.getEmail())
                .firstName(requestModel.getFirstName())
                .middleName(requestModel.getMiddleName())
                .lastName(requestModel.getLastName())
                .role(AccountType.EMPLOYEE == requestModel.getAccountType() ?
                        AccountRole.COMPANY : AccountRole.EMPLOYEE
                )
                .enabled(true)
                .build();
    }

    /**
     * Method convert message & account to response model. {@link ApiResponseModel}
     *
     * @param message message for response
     * @param account account from database;
     * @return {@link ApiResponseModel} with message & account data.
     */
    public ApiResponseModel toApiResponseModel(String message, Account account) {
        return ApiResponseModel.builder()
                .message(message)
                .accountDataModel(toAccountDataModel(account))
                .build();
    }

    /**
     * Method create account data for response from {@link Account}.
     *
     * @param account account from database.
     * @return {@link AccountDataModel} for response.
     */
    public AccountDataModel toAccountDataModel(Account account) {
        return AccountDataModel.builder()
                .username(account.getUsername())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .middleName(account.getMiddleName())
                .role(account.getRole().name())
                .email(account.getEmail())
                .birthData(account.getBirthData())
                .build();
    }
}
