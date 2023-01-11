/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.clientmanager.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Успешный ответ от сервиса.")
public class ApiResponseModel {

    @Schema(description = "Ответ от сервиса.", example = "Аккаунт успешно создан.")
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Данные пользователя", example = "Данные аккаунта.")
    private AccountDataModel accountDataModel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Schema(description = "Все пользователи", example = "Список аккаунтов.")
    private List<AccountDataModel> accounts;
}
