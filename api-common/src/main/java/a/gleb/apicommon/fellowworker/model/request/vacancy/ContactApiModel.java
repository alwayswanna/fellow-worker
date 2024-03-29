/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.request.vacancy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Контактная информация работодателя")
public class ContactApiModel {

    @NotEmpty
    @Schema(description = "ФИО", example = "Петров Игорь Васильевич")
    private String fio;

    @NotEmpty
    @Schema(description = "Номер телефона", example = "+79998087788")
    private String phone;

    @NotEmpty
    @Email
    @Schema(description = "Электронный адрес", example = "petrov@ya.ru")
    private String email;
}
