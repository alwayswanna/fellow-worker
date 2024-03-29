/*
 * Copyright (c) 12-3/1/23, 12:14 AM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.controller

import a.gleb.apicommon.fellowworker.model.request.vacancy.SearchVacancyApiModel
import a.gleb.apicommon.fellowworker.model.request.vacancy.VacancyApiModel
import a.gleb.apicommon.fellowworker.model.response.FellowWorkerResponseModel
import a.gleb.fellowworkerservice.configuration.OAUTH2_SECURITY_SCHEMA
import a.gleb.fellowworkerservice.service.VacancyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import java.util.*

const val VACANCY_TAG_NAME = "vacancy.tag.name"

@RestController
@RequestMapping("/api/v1/vacancy")
@Tag(name = VACANCY_TAG_NAME)
class VacancyController(
    private val vacancyService: VacancyService
) {

    @Operation(
        summary = "Создать вакансию.",
        security = [SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @PostMapping("/create")
    suspend fun create(@RequestBody @Valid request: VacancyApiModel): FellowWorkerResponseModel {
        return vacancyService.create(request)
    }

    @Operation(
        summary = "Редактировать вакансию.",
        security = [SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @PutMapping("/edit")
    suspend fun edit(@RequestBody @Valid request: VacancyApiModel): FellowWorkerResponseModel {
        return vacancyService.edit(request)
    }

    @Operation(
        summary = "Закрыть вакансию",
        security = [SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @DeleteMapping("/delete-id")
    suspend fun removeById(@RequestParam @NotNull vacancyId: UUID): FellowWorkerResponseModel {
        return vacancyService.deleteVacancy(vacancyId)
    }

    @Operation(summary = "Получить все вакансии")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @GetMapping("/vacancy-all")
    suspend fun getAllVacancy(): FellowWorkerResponseModel {
        return vacancyService.getAllVacancy()
    }

    @Operation(summary = "Получить вакансию по ID.")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @GetMapping("/id")
    suspend fun getVacancyById(@RequestParam @NotNull vacancyId: UUID): FellowWorkerResponseModel {
        return vacancyService.getVacancyById(vacancyId)
    }

    @Operation(
        summary = "Получить все вакансии текущего пользователя.",
        security = [SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)],
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @GetMapping("/current-user-vacancies")
    suspend fun getCurrentUserVacancies(): FellowWorkerResponseModel {
        return vacancyService.getCurrentUserVacancies()
    }

    @Operation(summary = "Поиск вакансий по определенным фильтрам")
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @PostMapping("/filter-vacancies")
    suspend fun filterVacancies(@RequestBody request: SearchVacancyApiModel): FellowWorkerResponseModel {
        return vacancyService.filter(request)
    }
}
