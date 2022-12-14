/*
 * Copyright (c) 12/28/22, 6:32 PM.
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
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Схема описания аккаунта.")
public class AccountDataModel {

    @Schema(description = "Имя пользователя", example = "username")
    private String username;

    @Schema(description = "Имя", example = "Олег")
    private String firstName;

    @Schema(description = "Отчество", example = "Игоревич")
    private String lastName;

    @Schema(description = "Фамилия", example = "Иванов")
    private String middleName;

    @Schema(description = "Тип аккаунта", example = "EMPLOYEE")
    private String role;

    @Schema(description = "Электронный адрес", example = "olegIv@mail.ru")
    private String email;

    @Schema(description = "Дата рождения", example = "22.03.1995")
    private LocalDate birthDate;
}
