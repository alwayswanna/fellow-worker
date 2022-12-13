package a.gleb.clientmanager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponseModel {

    @Schema(description = "Ответ от сервиса.", example = "Аккаунт успешно создан.")
    private String message;

    @JsonInclude(NON_NULL)
    @Schema(description = "Данные пользователя", example = "Данные аккаунта.")
    private AccountDataModel accountDataModel;

    @JsonInclude(NON_NULL)
    @Schema(description = "Все пользователи", example = "Список аккаунтов.")
    private List<AccountDataModel> accounts;
}
