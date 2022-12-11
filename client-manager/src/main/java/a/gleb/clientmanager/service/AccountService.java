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
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountModelMapper accountModelMapper;

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

    public ApiResponseModel editAccount(AccountRequestModel requestModel) {
        validateAccountDataInDataBase(requestModel);

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
