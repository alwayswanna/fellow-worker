/*
 * Copyright (c) 12-3/7/23, 10:06 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.controller

import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import a.gleb.apicommon.fellowworker.model.request.resume.SearchResumeApiModel
import a.gleb.apicommon.fellowworker.model.response.FellowWorkerResponseModel
import a.gleb.fellowworkerservice.configuration.OAUTH2_SECURITY_SCHEMA
import a.gleb.fellowworkerservice.service.ResumeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.*
import java.util.*

const val EMPLOYEE_TAG_NAME = "employee.tag.name"

@RestController
@RequestMapping("/api/v1/employee")
@Tag(name = EMPLOYEE_TAG_NAME)
class ResumeController(
    private val resumeService: ResumeService
) {

    @Operation(
        summary = "Создать резюме",
        security = [SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @PostMapping("/create-resume")
    suspend fun createResume(@RequestBody @Valid request: ResumeApiModel): FellowWorkerResponseModel {
        return resumeService.createResume(request)
    }

    @Operation(
        summary = "Редактировать резюме",
        security = [SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @PutMapping("/edit-resume")
    suspend fun editResume(@RequestBody @Valid request: ResumeApiModel): FellowWorkerResponseModel {
        return resumeService.editResume(request)
    }

    @Operation(
        summary = "Удалить резюме",
        security = [SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @DeleteMapping("/delete-resume")
    suspend fun deleteResume(@NotNull @RequestParam id: UUID): FellowWorkerResponseModel {
        return resumeService.deleteResume(id)
    }

    @Operation(
        summary = "Получить резюме по авторизованного пользователя",
        security = [SecurityRequirement(name = OAUTH2_SECURITY_SCHEMA)]
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @GetMapping("/current-user-resume")
    suspend fun getCurrentUserResume(): FellowWorkerResponseModel {
        return resumeService.getCurrentUserResume()
    }

    @Operation(
        summary = "Получить все резюме"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @GetMapping("/get-all-resume")
    suspend fun getAllResume(): FellowWorkerResponseModel {
        return resumeService.allResume()
    }

    @Operation(
        summary = "Получить резюме по ID"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @GetMapping("/get-resume-id")
    suspend fun getSingleResume(@NotNull @RequestParam id: UUID): FellowWorkerResponseModel {
        return resumeService.findResumeModelById(id)
    }

    @Operation(
        summary = "Поиск резюме по определенным фильтрам"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                description = "OK", responseCode = "200",
                content = [Content(
                    mediaType = APPLICATION_JSON_VALUE,
                    schema = Schema(implementation = FellowWorkerResponseModel::class)
                )]
            ),
            ApiResponse(description = "Bad request", responseCode = "400"),
            ApiResponse(description = "Internal server error", responseCode = "500")
        ]
    )
    @PostMapping("/filter-resumes")
    suspend fun filterResumes(@RequestBody @Valid request: SearchResumeApiModel): FellowWorkerResponseModel{
        return resumeService.filter(request)
    }
}