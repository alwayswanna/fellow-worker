package a.gleb.main_service.controller

import a.gleb.apicommon.api.main_service.resume.ResumeBriefResponse
import a.gleb.apicommon.api.main_service.resume.ResumeRequest
import a.gleb.apicommon.api.main_service.resume.ResumeResponse
import a.gleb.apicommon.api.main_service.resume.ResumeSearchRequest
import a.gleb.main_service.model.ApiResponse
import a.gleb.main_service.service.resume.ResumeService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
@RequestMapping("/api/v1/resumes")
@Tag(name = "Resumes", description = "Resume management APIs")
class ResumeRestController(
    private val resumeService: ResumeService
) {

    @PostMapping
    @Operation(
        summary = "Create a new resume",
    )
    @ResponseBody
    suspend fun createResume(@Valid @RequestBody request: ResumeRequest): ResumeResponse {
        return resumeService.createResume(request)
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resume by ID")
    @ResponseBody
    suspend fun getResumeById(@PathVariable id: UUID): ResumeResponse {
        return resumeService.getResumeById(id)
    }

    @GetMapping
    @Operation(summary = "Get all resumes with pagination")
    suspend fun getAllResumes(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @RequestParam(defaultValue = "createdAt") sortBy: String,
        @RequestParam(defaultValue = "desc") direction: String,
        @RequestParam(required = false) isActive: Boolean?
    ): ResponseEntity<ApiResponse<Page<ResumeResponse>>> {
        return resumeService.getAllResumes(page, size, sortBy, direction, isActive)
    }

    @PostMapping("/search")
    @Operation(summary = "Search resumes with filters")
    suspend fun searchResumes(@Valid @RequestBody searchRequest: ResumeSearchRequest):
            ResponseEntity<ApiResponse<Page<ResumeBriefResponse>>> {
        return resumeService.searchResumes(searchRequest)
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update resume by ID")
    suspend fun updateResume(
        @PathVariable id: UUID,
        @Valid @RequestBody request: ResumeRequest
    ): ResumeResponse {
        return resumeService.updateResume(id, request)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete resume by ID")
    suspend fun deleteResume(@PathVariable id: UUID) {
        return resumeService.deleteResume(id)
    }
}