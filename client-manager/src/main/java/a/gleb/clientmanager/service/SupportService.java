package a.gleb.clientmanager.service;

import a.gleb.apicommon.clientmanager.model.ApiResponseModel;
import a.gleb.clientmanager.exception.InvalidUserDataException;
import a.gleb.clientmanager.exception.UnexpectedErrorException;
import a.gleb.clientmanager.mapper.AccountModelMapper;
import a.gleb.oauth2persistence.db.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class SupportService {

    private final AccountRepository accountRepository;
    private final AccountModelMapper accountModelMapper;

    /**
     * Method which disable user account.
     *
     * @param userId id from request.
     * @return {@link ApiResponseModel} with message
     */
    public ApiResponseModel disable(UUID userId) {
        var account = accountRepository.findAccountById(userId)
                .orElseThrow(() -> new InvalidUserDataException(HttpStatus.BAD_REQUEST, "Аккаунт не найден"));
        account.setEnabled(false);

        try {
            accountRepository.save(account);
            return ApiResponseModel.builder()
                    .message("Аккаунт был успешно отключен.")
                    .build();
        } catch (Exception e) {
            log.error("Error while disable user account, error: {}, userId: {}",
                    e.getMessage(),
                    userId
            );
            throw new UnexpectedErrorException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Произошла ошибка при отключении аккаунта, попробуйте повторить попытку позже"
            );
        }
    }

    /**
     * Method which returns all accounts.
     *
     * @return {@link ApiResponseModel} with message & all accounts.
     */
    public ApiResponseModel getAllAccounts() {
        var accounts = accountRepository.findAll();
        var accountDataModels = accounts.stream().map(accountModelMapper::toAccountDataModel)
                .toList();

        return ApiResponseModel.builder()
                .message("Список аккаунтов успешно получен")
                .accounts(accountDataModels)
                .build();
    }

    /**
     * Method returns account by username.
     * @param username from request.
     * @return {@link ApiResponseModel} with message & account data.
     */
    public ApiResponseModel getAccountByUserName(String username) {
        var account = accountRepository.findAccountByUsernameOrEmail(username, username)
                .orElseThrow(() -> new InvalidUserDataException(HttpStatus.BAD_REQUEST, "Аккаунт не найден"));

        return accountModelMapper.toApiResponseModel("Аккаунт по запросу", account);
    }
}
