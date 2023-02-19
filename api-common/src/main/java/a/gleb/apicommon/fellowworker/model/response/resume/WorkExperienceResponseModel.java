/*
 * Copyright (c) 07-2/15/23, 11:40 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.response.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

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
    private String workingSpeciality;

    @Schema(description = "Трудовые обязанности", example = "Выход из границ.")
    private String responsibilities;
}
