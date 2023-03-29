/*
 * Copyright (c) 3-3/28/23, 10:13 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.clientmanager.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сменить секрет для клиента.")
public class ChangeClientCredentialsModel {

    @NotNull
    @Schema(description = "ID клиента", example = "message")
    private String clientId;

    @NotNull
    @Schema(description = "Новый секрет клиента", example = "secret")
    private String newSecret;
}
