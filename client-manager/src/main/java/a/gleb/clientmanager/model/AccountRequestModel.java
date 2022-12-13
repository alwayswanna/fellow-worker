package a.gleb.clientmanager.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequestModel {

    @Schema(description = "Имя пользователя", example = "username")
    private String username;

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
    private LocalDate localDate;
}
