package a.gleb.apicommon.fellowworker.model.response.vacancy;

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
@Schema(description = "Контактная информация.")
public class ContactResponseApiModel {

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
