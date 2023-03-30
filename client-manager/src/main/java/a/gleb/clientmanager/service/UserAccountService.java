/*
 * Copyright (c) 12-3/30/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.service;

import a.gleb.apicommon.clientmanager.model.AccountRequestModel;
import a.gleb.apicommon.clientmanager.model.ApiResponseModel;
import a.gleb.apicommon.clientmanager.model.ChangePasswordModel;
import a.gleb.clientmanager.exception.InvalidUserDataException;
import a.gleb.clientmanager.exception.UnexpectedErrorException;
import a.gleb.clientmanager.mapper.AccountModelMapper;
import a.gleb.clientmanager.service.db.AccountDatabaseService;
import a.gleb.oauth2persistence.db.dao.Account;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static a.gleb.clientmanager.utils.AccountChangeUtils.changeAccountData;

@Slf4j
@Service
@AllArgsConstructor
public class UserAccountService {

    private final AccountDatabaseService accountDatabaseService;
    private final PasswordEncoder passwordEncoder;
    private final AccountModelMapper accountModelMapper;
    private final AccountEntitiesService accountEntitiesService;
    private final OAuth2SecurityContextService oAuth2SecurityContextService;

    /**
     * Method creates new account.
     *
     * @param requestModel user request with data for new account.
     * @return {@link ApiResponseModel} response with message.
     */
    public ApiResponseModel createAccount(@NonNull AccountRequestModel requestModel) {
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
            accountDatabaseService.saveAccount(accountModelMapper.mapToAccount(requestModel));
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
    public ApiResponseModel editAccount(@NonNull AccountRequestModel requestModel) {
        if (requestModel.getUsername() != null || requestModel.getEmail() != null) {
            validateAccountDataInDataBase(requestModel);
        }
        var account = accountDatabaseService.findAccountById(oAuth2SecurityContextService.getUserId());
        /* fill data from request in the account fields  */
        changeAccountData(account, requestModel);

        try {
            accountDatabaseService.saveAccount(account);
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
            accountDatabaseService.deleteAccountById(userId);

            /* async call service with user entities */
            CompletableFuture.runAsync(() -> {
                accountEntitiesService.removeUserEntities(userId);
            });

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
    public ApiResponseModel getAccountData(@NonNull UUID userId) {
        var account = accountDatabaseService.findAccountById(userId);
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
        var account = accountDatabaseService.findAccountById(userId);

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
            accountDatabaseService.saveAccount(account);
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

    /**
     * Method extract from request JWT token {@link Account#getId()} and return current account data.
     *
     * @return {@link ApiResponseModel}
     */
    public ApiResponseModel getCurrentAccountData() {
        var account = accountDatabaseService.findAccountById(oAuth2SecurityContextService.getUserId());
        return accountModelMapper.toApiResponseModel("Данные аккаунта успешно получены", account);
    }

    /**
     * Method validate user data on duplicates, attempts to find account with similar user data.
     *
     * @param accountRequestModel model with user data
     */
    private void validateAccountDataInDataBase(@NonNull AccountRequestModel accountRequestModel) {
        var userFromDatabase = accountDatabaseService.findAccountByUsernameOrEmail(
                accountRequestModel.getUsername(),
                accountRequestModel.getEmail()
        );

        if (userFromDatabase != null) {
            var reason = userFromDatabase.getUsername().equalsIgnoreCase(accountRequestModel.getUsername()) ?
                    "Пользователь с данным username существует." : "Пользователь с данным email существует.";
            throw new InvalidUserDataException(HttpStatus.BAD_REQUEST, reason);
        }
    }
}
