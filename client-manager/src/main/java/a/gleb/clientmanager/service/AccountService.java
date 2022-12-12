package a.gleb.clientmanager.service;

import a.gleb.clientmanager.exception.InvalidUserDataException;
import a.gleb.clientmanager.exception.UnexpectedErrorException;
import a.gleb.clientmanager.mapper.AccountModelMapper;
import a.gleb.clientmanager.model.AccountRequestModel;
import a.gleb.clientmanager.model.ApiResponseModel;
import a.gleb.oauth2persistence.db.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static a.gleb.clientmanager.utils.AccountChangeUtils.changeAccountData;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountModelMapper accountModelMapper;
    private final OAuth2SecurityContextService oAuth2SecurityContextService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Method creates new account.
     * @param requestModel user request with data for new account.
     * @return {@link ApiResponseModel} response with message.
     */
    public ApiResponseModel createAccount(AccountRequestModel requestModel) {
        validateAccountDataInDataBase(requestModel);

        try {
            accountRepository.save(accountModelMapper.mapToAccount(requestModel));
            return ApiResponseModel.builder()
                    .message("Ваш аккаунт успешно создан")
                    .build();
        } catch (Exception e) {
            log.error("Error while save new account to database, {}", e.getMessage());
            throw new UnexpectedErrorException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Произошла ошибка при создании аккаунта, попробуйте повторить попытку позже"
            );
        }
    }

    /**
     * Method edit existing account with check on unique username.
     * @param requestModel request from frontend
     * @return {@link ApiResponseModel} response for user.
     */
    public ApiResponseModel editAccount(AccountRequestModel requestModel) {
        validateAccountDataInDataBase(requestModel);
        var account = accountRepository.findAccountById(oAuth2SecurityContextService.getUserId())
                .orElseThrow(() -> new InvalidUserDataException(HttpStatus.BAD_REQUEST, "Неверно введены данные."));
        /* fill data from request in the account fields  */
        changeAccountData(account, requestModel);
        /* encode password */
        account.setPassword(passwordEncoder.encode(account.getPassword()));

        try {
            accountRepository.save(account);
            return ApiResponseModel.builder()
                    .message("Данные вашего аккаунт успешно изменены")
                    .build();
        } catch (Exception e) {
            log.error("Error while save new account to database, {}", e.getMessage());
            throw new UnexpectedErrorException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Произошла ошибка при создании аккаунта, попробуйте повторить попытку позже"
            );
        }
    }

    public ApiResponseModel deleteAccount() {

        return ApiResponseModel.builder().build();
    }


    private void validateAccountDataInDataBase(AccountRequestModel accountRequestModel) {
        var userFromDatabase = accountRepository
                .findAccountByUsernameOrEmail(accountRequestModel.getUsername(), accountRequestModel.getEmail());
        if (userFromDatabase.isPresent()) {
            var reason = userFromDatabase.get().getUsername().equalsIgnoreCase(accountRequestModel.getUsername()) ?
                    "Пользователь с данным username существует." : "Пользователь с данным email существует.";
            throw new InvalidUserDataException(HttpStatus.BAD_REQUEST, reason);
        }
    }
}
