/*
 * Copyright (c) 07-3/6/23, 10:57 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.response.resume;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Модель для созданного резюме")
public class ResumeResponseModel {

    @NotNull
    @Schema(description = "Идентификатор")
    private UUID resumeId;

    @Schema(description = "Идентификатор пользователя")
    private UUID userId;

    @NotEmpty
    @Schema(description = "Имя", example = "Аркадий")
    private String firstName;

    @NotEmpty
    @Schema(description = "Фамилия", example = "Нахимов")
    private String middleName;

    @Schema(description = "Отчество", example = "Олегович")
    private String lastName;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата рождения", example = "1985-11-03")
    private LocalDate birthDate;

    @NotEmpty
    @Schema(description = "Желаемая должность", example = "Начальник склада")
    private String job;

    @Schema(description = "Ожидаемая заработная плата", example = "40000")
    private String expectedSalary;

    @Schema(description = "О себе", example = "Увлекаюсь ... Хобби")
    private String about;

    @Schema(description = "Город", example = "Новосибирск")
    private String city;

    @Valid
    @Schema(description = "Информация об образовании")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<EducationResponseModel> education;

    @NotEmpty
    @Schema(description = "Профессиональные навыки")
    private List<String> professionalSkills;

    @Valid
    @Schema(description = "История работы")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<WorkExperienceResponseModel> workingHistory;

    @Valid
    @Schema(description = "Контактная информация")
    private ContactResponseModel contact;

    @Schema(description = "Время последнего обновления")
    private LocalDateTime lastUpdate;
}
