/*
 * Copyright (c) 12/28/22, 7:57 PM.
 * Created by https://github.com/alwayswanna
 *
 */

package a.gleb.apicommon.clientmanager.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Смена данных учетной записи.")
public class AccountRequestModel {

    @Schema(description = "Имя пользователя", example = "username")
    private String username;

    @Schema(description = "Пароль пользователя", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String password;

    @Schema(description = "Электронный адрес", example = "olegIv@mail.ru")
    private String email;

    @Schema(description = "Имя", example = "Олег")
    private String firstName;

    @Schema(description = "Фамилия", example = "Иванов")
    private String middleName;

    @Schema(description = "Отчество", example = "Игоревич")
    private String lastName;

    @Schema(description = "Тип аккаунта", example = "COMPANY")
    private AccountType accountType;

    @Schema(description = "Дата рождения", example = "22.03.1995")
    private LocalDate birthDate;
}
