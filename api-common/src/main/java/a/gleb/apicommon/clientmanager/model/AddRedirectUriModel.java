/*
 * Copyright (c) 3-3/28/23, 11:06 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.clientmanager.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Добавить redirect-URI для клиента.")
public class AddRedirectUriModel {

    @NotNull
    @Schema(description = "ID клиента", example = "message")
    private String clientId;

    @NotEmpty
    @Schema(description = "Список новых URI")
    private List<String> redirectUris;
}
