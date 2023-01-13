/*
 * Copyright (c) 07-1/11/23, 10:19 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.request.resume;

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
@Schema(description = "История работы.")
public class WorkExperienceApiModel {

    @NotNull
    @Schema(description = "Дата трудоустройства", example = "2021-09-01")
    private LocalDate startTime;

    @NotNull
    @Schema(description = "Дата увольнения", example = "2022-06-01")
    private LocalDate endTime;

    @NotEmpty
    @Schema(description = "Наименование компании", example = "Уфимский промышленный корпус")
    private String companyName;

    @NotEmpty
    @Schema(description = "Занимаемая должность", example = "Заведующий хоз. учета")
    private String workingSpecialty;

    @Schema(description = "Трудовые обязанности", example = "Выход за границы дозволенного.")
    private String responsibilities;
}
