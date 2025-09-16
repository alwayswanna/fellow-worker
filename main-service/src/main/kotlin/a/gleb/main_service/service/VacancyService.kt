package a.gleb.main_service.service

import a.gleb.apicommon.api.main_service.resume.StatusUpdateRequest
import a.gleb.apicommon.api.main_service.vacancy.VacancyBriefResponse
import a.gleb.apicommon.api.main_service.vacancy.VacancyRequest
import a.gleb.apicommon.api.main_service.vacancy.VacancyResponse
import a.gleb.apicommon.api.main_service.vacancy.VacancySearchRequest
import a.gleb.main_service.db.repository.VacancyEntityRepository
import a.gleb.main_service.mapper.VacancyMapper
import a.gleb.main_service.model.ApiResponse
import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody

private val logger = KotlinLogging.logger {}

@Service
class VacancyService(
    private val vacancyMapper: VacancyMapper,
    private val vacancyEntityRepository: VacancyEntityRepository
) {

    suspend fun createVacancy(request: VacancyRequest): ResponseEntity<ApiResponse<VacancyResponse>> {
        TODO()
    }

    suspend fun getVacancyById(@PathVariable id: Long): ResponseEntity<ApiResponse<VacancyResponse>> {
        TODO()
    }

    suspend fun getAllVacancies(
        page: Int,
        size: Int,
        sortBy: String,
        direction: String,
        isActive: Boolean?,
        isRemote: Boolean?
    ): ResponseEntity<ApiResponse<Page<VacancyResponse>>> {
        TODO()
    }

    suspend fun searchVacancies(@Valid @RequestBody searchRequest: VacancySearchRequest): ResponseEntity<ApiResponse<Page<VacancyBriefResponse>>> {
        TODO()
    }

    suspend fun updateVacancy(id: Long, request: VacancyRequest): ResponseEntity<ApiResponse<VacancyResponse>> {
        TODO()
    }

    suspend fun deleteVacancy(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        TODO()
    }

    suspend fun updateVacancyStatus(
        id: Long,
        request: StatusUpdateRequest
    ): ResponseEntity<ApiResponse<VacancyResponse>> {
        TODO()
    }

    suspend fun getVacanciesByCompany(
        companyName: String,
        page: Int,
        size: Int
    ): ResponseEntity<ApiResponse<Page<VacancyBriefResponse>>> {
        TODO()
    }

    suspend fun getExpiringSoonVacancies(
        days: Int,
        page: Int,
        size: Int
    ): ResponseEntity<ApiResponse<Page<VacancyBriefResponse>>> {
        TODO()
    }
}