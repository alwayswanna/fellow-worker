/*
 * Copyright (c) 1-1/10/23, 11:08 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.request.resume;


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
@Schema(description = "Контактная информация соискателя")
public class ContactResumeApiModel {

    @NotEmpty
    @Schema(description = "Номер телефона", example = "+79008005544")
    private String phone;

    @NotEmpty
    @Email
    @Schema(description = "Электронный адрес", example = "worker_mail@yandex.ru")
    private String email;
}
