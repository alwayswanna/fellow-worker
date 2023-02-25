/*
 * Copyright (c) 2-3/7/23, 10:06 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.request.vacancy;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель для поиска вакансии")
public class SearchVacancyApiModel {

    @Schema(description = "Город", example = "Новосибирск")
    private String city;

    @Schema(description = "Тип занятости (Полная/Частичная)", example = "FULL_EMPLOYMENT")
    private WorkType typeOfWork;

    @Schema(description = "Формат работы (Удаленно/Офис)", example = "OFFICE")
    private TypeOfWorkPlacement typeOfWorkPlacement;

    @Schema(description = "Ключевые навыки", example = "Java, Kotlin")
    private String keySkills;
}
