/*
 * Copyright (c) 12-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.apicommon.fellowworker.model.response;

import a.gleb.apicommon.fellowworker.model.response.resume.ResumeResponseModel;
import a.gleb.apicommon.fellowworker.model.response.vacancy.VacancyResponseApiModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Ответ от сервиса")
public class FellowWorkerResponseModel {

    @JsonInclude(NON_NULL)
    private String message;

    @JsonInclude(NON_NULL)
    private ResumeResponseModel resumeResponse;

    @JsonInclude(NON_NULL)
    private List<ResumeResponseModel> resumes;

    @JsonInclude(NON_NULL)
    private VacancyResponseApiModel vacancyResponse;

    @JsonInclude(NON_NULL)
    private List<VacancyResponseApiModel> vacancies;
}
