/*
 * Copyright (c) 12-1/24/23, 10:30 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.service;

import a.gleb.apicommon.clientmanager.model.AccountRequestModel;
import a.gleb.apicommon.clientmanager.model.ApiResponseModel;
import a.gleb.apicommon.clientmanager.model.ChangePasswordModel;
import a.gleb.clientmanager.exception.InvalidUserDataException;
import a.gleb.clientmanager.exception.UnexpectedErrorException;
import a.gleb.clientmanager.mapper.AccountModelMapper;
import a.gleb.oauth2persistence.db.dao.Account;
import a.gleb.oauth2persistence.db.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
     *
     * @param requestModel user request with data for new account.
     * @return {@link ApiResponseModel} response with message.
     */
    public ApiResponseModel createAccount(AccountRequestModel requestModel) {
        if (requestModel.getPassword().isEmpty()) {
            throw new InvalidUserDataException(HttpStatus.BAD_REQUEST, "Невозможно создать пользователя без пароля.");
        }
        if (requestModel.getUsername().isEmpty() || requestModel.getFirstName().isEmpty() ||
                requestModel.getMiddleName().isEmpty() || requestModel.getBirthDate() == null ||
                requestModel.getEmail().isEmpty()) {
            throw new InvalidUserDataException(
                    HttpStatus.BAD_REQUEST,
                    "Невозможно создать пользователя, недостаточно данных."
            );
        }

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
     *
     * @param requestModel request from frontend
     * @return {@link ApiResponseModel} response for user.
     */
    public ApiResponseModel editAccount(AccountRequestModel requestModel) {
        validateAccountDataInDataBase(requestModel);
        var account = accountRepository.findAccountById(oAuth2SecurityContextService.getUserId())
                .orElseThrow(() -> new InvalidUserDataException(HttpStatus.BAD_REQUEST, "Неверно введены данные."));
        /* fill data from request in the account fields  */
        changeAccountData(account, requestModel);

        try {
            accountRepository.save(account);
            return ApiResponseModel.builder()
                    .message("Данные вашего аккаунт успешно обновлены.")
                    .build();
        } catch (Exception e) {
            log.error("Error while edit account to database, {}", e.getMessage());
            throw new UnexpectedErrorException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Произошла ошибка при редактировании аккаунта, попробуйте повторить попытку позже"
            );
        }
    }

    /**
     * Method delete account by ID from user token.
     *
     * @return {@link ApiResponseModel} response with message.
     */
    public ApiResponseModel deleteAccount() {
        var userId = oAuth2SecurityContextService.getUserId();
        try {
            accountRepository.deleteById(userId);
            return ApiResponseModel.builder()
                    .message("Ваш аккаунт был успешно удален.")
                    .build();
        } catch (Exception e) {
            log.error("Error while delete account to database, {}, userId: {}",
                    e.getMessage(),
                    userId
            );
            throw new UnexpectedErrorException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Произошла ошибка при удалении аккаунта, попробуйте повторить попытку позже"
            );
        }
    }

    /**
     * Method returns account data.
     *
     * @param userId id from user request.
     * @return {@link ApiResponseModel} with data for user by id.
     */
    public ApiResponseModel getAccountData(UUID userId) {
        var account = accountRepository.findAccountById(userId)
                .orElseThrow(
                        () -> new InvalidUserDataException(
                                HttpStatus.BAD_REQUEST, "Пользователь с таким id не найден")
                );
        return accountModelMapper.toApiResponseModel("Данные аккаунта успешно получены", account);
    }

    /**
     * Method change account password, after validate password.
     *
     * @param changePasswordModel model with old and new password.
     * @return {@link ApiResponseModel} with message.
     */
    public ApiResponseModel changeUserPassword(ChangePasswordModel changePasswordModel) {
        var userId = oAuth2SecurityContextService.getUserId();
        var account = accountRepository.findAccountById(userId).orElseThrow(
                () -> new InvalidUserDataException(
                        HttpStatus.BAD_REQUEST, "Пользователь с таким id не найден")
        );
        /* compare current password in database with password from request */
        if (passwordEncoder.matches(changePasswordModel.getOldPassword(), account.getPassword())) {
            account.setPassword(passwordEncoder.encode(changePasswordModel.getNewPassword()));
        } else {
            throw new InvalidUserDataException(
                    HttpStatus.BAD_REQUEST,
                    String.format(
                            "Текущий пароль не совпадает с текущим, введенный пароль: %s",
                            changePasswordModel.getOldPassword()
                    )
            );
        }

        try {
            accountRepository.save(account);
            return ApiResponseModel.builder()
                    .message("Ваш пароль успешно изменен")
                    .build();
        } catch (Exception e) {
            log.error("Error while change password, error: {}, userId: {}",
                    e.getMessage(),
                    userId
            );
            throw new UnexpectedErrorException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Произошла ошибка при при смене пароля, попробуйте повторить попытку позже"
            );
        }
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

    /**
     * Method extract from request JWT token {@link Account#getId()} and return current account data.
     *
     * @return {@link ApiResponseModel}
     */
    public ApiResponseModel getCurrentAccountData() {
        var account = accountRepository.findAccountById(oAuth2SecurityContextService.getUserId())
                .orElseThrow(() -> new InvalidUserDataException(HttpStatus.BAD_REQUEST, "Неверно введены данные."));
        return accountModelMapper.toApiResponseModel("Данные аккаунта успешно получены", account);
    }
}
