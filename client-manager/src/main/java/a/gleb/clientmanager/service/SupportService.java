/*
 * Copyright (c) 07-3/28/23, 11:06 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.clientmanager.service;

import a.gleb.apicommon.clientmanager.model.AddRedirectUriModel;
import a.gleb.apicommon.clientmanager.model.ApiResponseModel;
import a.gleb.apicommon.clientmanager.model.ChangeClientCredentialsModel;
import a.gleb.clientmanager.exception.InvalidUserDataException;
import a.gleb.clientmanager.exception.UnexpectedErrorException;
import a.gleb.clientmanager.mapper.AccountModelMapper;
import a.gleb.oauth2persistence.db.repository.AccountRepository;
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

    private final AccountRepository accountRepository;
    private final AccountModelMapper accountModelMapper;
    private final AccountEntitiesService accountEntitiesService;
    private final RegisteredClientService registeredClientService;

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
     *
     * @param username from request.
     * @return {@link ApiResponseModel} with message & account data.
     */
    public ApiResponseModel getAccountByUserName(String username) {
        var account = accountRepository.findAccountByUsernameOrEmail(username, username)
                .orElseThrow(() -> new InvalidUserDataException(HttpStatus.BAD_REQUEST, "Аккаунт не найден"));

        return accountModelMapper.toApiResponseModel("Аккаунт по запросу", account);
    }

    /**
     * Method remove user and user entities by id
     *
     * @param accountId user id from request
     * @return {@link ApiResponseModel} with message.
     */
    public ApiResponseModel removeUserAndUserEntitiesById(UUID accountId) {
        var account = accountRepository.findAccountById(accountId)
                .orElseThrow(() -> new InvalidUserDataException(HttpStatus.BAD_REQUEST, "Аккаунт не найден"));

        try {
            accountRepository.delete(account);
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
            registeredClientService.changeUserSecretForClient(request.getClientId(), request.getNewSecret());
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
        try{
            registeredClientService.addedNewRedirectUrisForClient(request.getClientId(), request.getRedirectUris());
            return ApiResponseModel.builder()
                    .message(format(
                            "Секретный ключ клиента %s, был успешно сменен. Добавлены пути: %s",
                            request.getClientId(),
                            request.getRedirectUris()
                    ))
                    .build();
        }catch (Exception e){
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
