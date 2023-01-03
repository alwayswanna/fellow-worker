/*
 * Copyright (c) 12/24/22, 10:24 PM.
 * Created by https://github.com/alwayswanna
 *
 */

package a.gleb.apicommon.fellowworker.model.response.vacancy;

import a.gleb.apicommon.fellowworker.model.request.vacancy.ContactApiModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.NOT_REQUIRED;

@Getter
@Setter
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ для на запрос вакансии.")
public class VacancyResponseApiModel {

    @JsonInclude(NON_NULL)
    @Schema(description = "Идентификатор вакансии для редактирования", requiredMode = NOT_REQUIRED)
    private UUID resumeId;

    @NotEmpty
    @Schema(description = "Название вакансии", example = "Сварщик")
    private String vacancyName;

    @NotNull
    @Schema(description = "Тип работы", example = "FULL_EMPLOYMENT")
    private String typeOfWork;

    @NotNull
    @Schema(description = "Тип расположение", example = "OFFICE")
    private String typeOfWorkPlacement;

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

    @Schema(description = "Последнее обновление")
    private LocalDateTime lastUpdate;
}
