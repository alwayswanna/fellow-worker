package a.gleb.main_service.controller

import a.gleb.apicommon.api.main_service.resume.StatusUpdateRequest
import a.gleb.apicommon.api.main_service.vacancy.VacancyBriefResponse
import a.gleb.apicommon.api.main_service.vacancy.VacancyRequest
import a.gleb.apicommon.api.main_service.vacancy.VacancyResponse
import a.gleb.apicommon.api.main_service.vacancy.VacancySearchRequest
import a.gleb.main_service.model.ApiResponse
import a.gleb.main_service.service.VacancyService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/vacancies")
@Tag(name = "Vacancies", description = "Vacancy management APIs")
class VacancyRestController(
    private val vacancyService: VacancyService
) {

    @PostMapping
    @Operation(summary = "Create a new vacancy")
    suspend fun createVacancy(@Valid @RequestBody request: VacancyRequest): ResponseEntity<ApiResponse<VacancyResponse>> {
        return vacancyService.createVacancy(request)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get vacancy by ID")
    suspend fun getVacancyById(@PathVariable id: Long): ResponseEntity<ApiResponse<VacancyResponse>> {
        return vacancyService.getVacancyById(id)
    }

    @GetMapping
    @Operation(summary = "Get all vacancies with pagination")
    suspend fun getAllVacancies(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") direction: String,
        @RequestParam(required = false) isActive: Boolean?,
        @RequestParam(required = false) isRemote: Boolean?
    ): ResponseEntity<ApiResponse<Page<VacancyResponse>>> {
        return vacancyService.getAllVacancies(page, size, sortBy, direction, isActive, isRemote)
    }

    @PostMapping("/search")
    @Operation(summary = "Search vacancies with filters")
    suspend fun searchVacancies(@Valid @RequestBody searchRequest: VacancySearchRequest): ResponseEntity<ApiResponse<Page<VacancyBriefResponse>>> {
        return vacancyService.searchVacancies(searchRequest)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update vacancy by ID")
    suspend fun updateVacancy(
        @PathVariable id: Long,
        @Valid @RequestBody request: VacancyRequest
    ): ResponseEntity<ApiResponse<VacancyResponse>> {
        return vacancyService.updateVacancy(id, request)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete vacancy by ID")
    suspend fun deleteVacancy(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        return vacancyService.deleteVacancy(id)
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update vacancy status")
    suspend fun updateVacancyStatus(
        @PathVariable id: Long,
        @Valid @RequestBody request: StatusUpdateRequest
    ): ResponseEntity<ApiResponse<VacancyResponse>> {
        return vacancyService.updateVacancyStatus(id, request)
    }

    @GetMapping("/company/{companyName}")
    @Operation(summary = "Get vacancies by company name")
    suspend fun getVacanciesByCompany(
        @PathVariable companyName: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<Page<VacancyBriefResponse>>> {
        return vacancyService.getVacanciesByCompany(companyName, page, size)
    }

    @GetMapping("/active/expiring-soon")
    @Operation(summary = "Get active vacancies expiring soon")
    suspend fun getExpiringSoonVacancies(
        @RequestParam(defaultValue = "7") days: Int,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<Page<VacancyBriefResponse>>> {
        return vacancyService.getExpiringSoonVacancies(days, page, size)
    }
}