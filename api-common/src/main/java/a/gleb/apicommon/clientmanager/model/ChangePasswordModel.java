package a.gleb.apicommon.clientmanager.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Validated
@Schema(description = "Модель запроса на смену пароля.")
public class ChangePasswordModel {

    @Schema(description = "Старый пароль", example = "old-password")
    @NotEmpty
    private String oldPassword;

    @Schema(description = "Новый пароль", example = "new-password")
    @NotEmpty
    private String newPassword;
}
