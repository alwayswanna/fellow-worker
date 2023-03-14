/*
 * Copyright (c) 3-3/7/23, 10:06 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.request.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель для поиска резюме")
public class SearchResumeApiModel {

    @Schema(description = "Город", example = "Москва")
    private String city;

    @Schema(description = "Ключевые навыки", example = "CasandraDB")
    private String keySkills;

    @Schema(description = "Специальность", example = "Java, Kotlin")
    private String job;

    @Schema(description = "Ожидаемая заработная плата не более:", example = "40000")
    private String salary;
}
