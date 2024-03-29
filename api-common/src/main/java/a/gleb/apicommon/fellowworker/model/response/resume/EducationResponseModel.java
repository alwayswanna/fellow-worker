/*
 * Copyright (c) 07-2/7/23, 10:48 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.response.resume;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@Validated
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сведения об образовании в ответе")
public class EducationResponseModel {

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата поступления в уч. заведение", example = "2016-09-01")
    private LocalDate startTime;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Дата окончания уч. заведения", example = "2021-06-01")
    private LocalDate endTime;

    @NotEmpty
    @Schema(description = "Наименование учебного заведения", example = "Башкирский государственный университет")
    private String educationalInstitution;

    @NotNull
    @Schema(description = "Ученая степень")
    private EducationLevel educationLevel;

    public enum EducationLevel {
        BACHELOR,
        SPECIALTY,
        MAGISTRACY
    }
}
