/*
 * Copyright (c) 12-12/25/22, 11:09 PM.
 * Created by https://github.com/alwayswanna
 *
 */

package a.gleb.apicommon.fellowworker.model.request.vacancy;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Информация о вакансии")
public class VacancyApiModel {

    @Schema(description = "Идентификатор вакансии")
    private UUID vacancyId;

    @NotEmpty
    @Schema(description = "Название вакансии", example = "Сварщик")
    private String vacancyName;

    @NotNull
    @Schema(description = "Тип работы", example = "FULL_EMPLOYMENT")
    private TypeOfWork typeOfWork;

    @NotNull
    @Schema(description = "Тип расположение", example = "OFFICE")
    private TypeOfWorkPlacement typeOfWorkPlacement;

    @NotEmpty
    @Schema(description = "Название компании", example = "ООО СварТехКомплект")
    private String companyName;

    @NotEmpty
    @Schema(description = "Адрес юр.лица", example = "г. Москва Южное Бутово, Проспект молодежи 3/1")
    private String companyFullAddress;

    @NotNull
    @Schema(description = "Ключевые требуемые навыки")
    private List<String> keySkills;

    @NotEmpty
    @Schema(description = " Город, место работы", example = "Москва")
    private String cityName;

    @NotEmpty
    @Schema(description = "Рабочие обязанности")
    private List<String> workingResponsibilities;

    @Schema(description = "Бонусы компании")
    private List<String> companyBonuses;

    @Schema(description = "Контактные данные")
    private ContactApiModel contactApiModel;

    @Schema(description = "Тип работы (по занятости)")
    public enum TypeOfWork {

        FULL_EMPLOYMENT,
        PART_TIME_EMPLOYMENT,
    }

    @Schema(description = "Тип работы (по месту)")
    public enum TypeOfWorkPlacement {

        OFFICE,
        REMOTE,
    }
}
