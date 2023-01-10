/*
 * Copyright (c) 12-1/10/23, 11:08 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.request.resume;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
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
@Schema(description = "Модель для создания резюме")
public class ResumeApiModel {

    @JsonInclude(NON_NULL)
    @Schema(description = "Идентификатор резюме для редактирования", requiredMode = NOT_REQUIRED)
    private UUID resumeId;

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

    @Valid
    @Schema(description = "Информация об образовании")
    private List<EducationApiModel> education;

    @NotEmpty
    @Schema(description = "Профессиональные навыки")
    private List<String> professionalSkills;

    @Valid
    @Schema(description = "История работы")
    private List<WorkExperienceApiModel> workingHistory;

    @Valid
    @Schema(description = "Контактная информация соискателя")
    private ContactResumeApiModel contact;

    private String base64Image;

    private String extensionPostfix;
}
