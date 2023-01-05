/*
 * Copyright (c) 07-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.response.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;


@Getter
@Setter
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "История в ответе.")
public class WorkExperienceResponseModel {

    @NotNull
    @Schema(description = "Дата трудоустройства", example = "2021-09-01")
    private LocalDate startTime;

    @NotNull
    @Schema(description = "Дата увольнения", example = "2022-06-01")
    private LocalDate endTime;

    @NotEmpty
    @Schema(description = "Наименование компании")
    private String companyName;

    @NotEmpty
    @Schema(description = "Занимаемая должность", example = "Заведующий хоз. учета")
    private String workingSpecialty;

    @Schema(description = "Трудовые обязанности")
    private List<String> responsibilities;

    @Schema(description = "Горячие слова для поиска")
    private List<String> tags;
}
