/*
 * Copyright (c) 07-3/30/23, 11:14 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.service;

import a.gleb.apicommon.clientmanager.model.AddRedirectUriModel;
import a.gleb.apicommon.clientmanager.model.ApiResponseModel;
import a.gleb.apicommon.clientmanager.model.ChangeClientCredentialsModel;
import a.gleb.clientmanager.exception.InvalidUserDataException;
import a.gleb.clientmanager.exception.UnexpectedErrorException;
import a.gleb.clientmanager.mapper.AccountModelMapper;
import a.gleb.clientmanager.service.db.AccountDatabaseService;
import a.gleb.clientmanager.service.db.RegisteredClientDatabaseService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.lang.String.format;

@Slf4j
@Service
@AllArgsConstructor
public class SupportService {

    private final AccountDatabaseService accountDatabaseService;
    private final AccountModelMapper accountModelMapper;
    private final AccountEntitiesService accountEntitiesService;
    private final RegisteredClientDatabaseService registeredClientDatabaseService;

    /**
     * Method disable user account.
     *
     * @param userId id from request.
     * @return {@link ApiResponseModel} with message
     */
    public ApiResponseModel disable(UUID userId) {
        var account = accountDatabaseService.findAccountById(userId);
        account.setEnabled(false);

        try {
            accountDatabaseService.saveAccount(account);
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
        var accounts = accountDatabaseService.findAllAccounts();
        var accountDataModels = accounts.stream().map(accountModelMapper::toAccountDataModel)
                .toList();

        return ApiResponseModel.builder()
                .message("Список аккаунтов успешно получен")
                .accounts(accountDataModels)
                .build();
    }

    /**
     * Method returns account by username.
     *
     * @param username from request.
     * @return {@link ApiResponseModel} with message & account data.
     */
    public ApiResponseModel getAccountByUserName(String username) {
        var account = accountDatabaseService.findAccountByUsernameOrEmail(username, username);
        if (account == null) throw new InvalidUserDataException(HttpStatus.BAD_REQUEST, "Аккаунт не найден");

        return accountModelMapper.toApiResponseModel("Аккаунт по запросу", account);
    }

    /**
     * Method remove user and user entities by id
     *
     * @param accountId user id from request
     * @return {@link ApiResponseModel} with message.
     */
    public ApiResponseModel removeUserAndUserEntitiesById(UUID accountId) {
        var account = accountDatabaseService.findAccountById(accountId);

        try {
            accountDatabaseService.deleteAccountById(account.getId());
            accountEntitiesService.removeUserEntities(account.getId());

            return ApiResponseModel.builder().message("Данные успешно удалены").build();
        } catch (Exception e) {
            log.error("Error while remove user and user account");
            throw new UnexpectedErrorException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    format(
                            "Произошла ошибка при удалении аккаунта, попробуйте повторить попытку позже. Ошибка: %s",
                            e.getMessage()
                    )
            );
        }
    }

    /**
     * Method changed clientSecret for registeredClient with selected clientId.
     *
     * @param request model from request {@link ChangeClientCredentialsModel}
     */
    public ApiResponseModel updateRegisteredClientSecret(ChangeClientCredentialsModel request) {
        try {
            registeredClientDatabaseService.changeUserSecretForClient(request.getClientId(), request.getNewSecret());
            return ApiResponseModel.builder()
                    .message(format(
                            "Секретный ключ клиента %s, был успешно сменен. Новый ключ: %s",
                            request.getClientId(),
                            request.getNewSecret()
                    ))
                    .build();
        } catch (Exception e) {
            log.error("Error while remove client secret for registered client. [clientId = {}]", request.getClientId());
            throw new UnexpectedErrorException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    format(
                            "Произошла ошибка при смены секрета, клиента. Ошибка: %s",
                            e.getMessage()
                    )
            );
        }
    }

    /**
     * Method added new redirect URIs for client with clientId
     *
     * @param request request model with new redirect URIs {@link AddRedirectUriModel}
     */
    public ApiResponseModel addNewRedirectUrisToClient(AddRedirectUriModel request) {
        try {
            registeredClientDatabaseService.addedNewRedirectUrisForClient(request.getClientId(), request.getRedirectUris());
            return ApiResponseModel.builder()
                    .message(format(
                            "Секретный ключ клиента %s, был успешно сменен. Добавлены пути: %s",
                            request.getClientId(),
                            request.getRedirectUris()
                    ))
                    .build();
        } catch (Exception e) {
            log.error("Error while add new redirect uris for client. [clientId = {}]", request.getClientId());
            throw new UnexpectedErrorException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    format(
                            "Произошла ошибка при добавлении редирект путей клиента. Ошибка: %s",
                            e.getMessage()
                    )
            );
        }
    }
}
