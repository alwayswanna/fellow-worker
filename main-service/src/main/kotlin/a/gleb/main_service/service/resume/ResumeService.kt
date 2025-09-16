package a.gleb.main_service.service.resume

import a.gleb.apicommon.api.main_service.resume.ResumeBriefResponse
import a.gleb.apicommon.api.main_service.resume.ResumeRequest
import a.gleb.apicommon.api.main_service.resume.ResumeResponse
import a.gleb.apicommon.api.main_service.resume.ResumeSearchRequest
import a.gleb.apicommon.event.EventType
import a.gleb.main_service.db.entity.resume.Resume
import a.gleb.main_service.db.repository.resume.ResumeRepository
import a.gleb.main_service.exception.ResumeConflictException
import a.gleb.main_service.exception.ResumeNotFoundException
import a.gleb.main_service.mapper.resume.ResumeMapper
import a.gleb.main_service.model.ApiResponse
import a.gleb.main_service.service.resume.sub.ResumeEducationService
import a.gleb.main_service.service.resume.sub.ResumeExperienceService
import a.gleb.main_service.service.resume.sub.ResumeSkillService
import mu.KotlinLogging
import org.springframework.data.domain.Page
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class ResumeService(
    private val resumeMapper: ResumeMapper,
    private val resumeRepository: ResumeRepository,
    private val resumeSkillService: ResumeSkillService,
    private val resumeEducationService: ResumeEducationService,
    private val resumeExperienceService: ResumeExperienceService,
    private val resumeOutboxEventService: ResumeOutboxEventService,
) {

    /**
     * Create new resume entity
     * @param request data to create new resume entity
     * @throws ResumeConflictException if process of creation was failed
     * @return [ResumeResponse] data of created resume
     */
    @Transactional
    suspend fun createResume(request: ResumeRequest): ResumeResponse {
        logger.debug { "Request to create resume: $request" }

        val entity: Resume
        try {
            entity = resumeRepository.save(resumeMapper.toEntity(request))

            resumeEducationService.create(entity.id!!, request.educations)
            resumeSkillService.create(entity.id!!, request.skills)
            resumeExperienceService.create(entity.id!!, request.experiences)

            resumeOutboxEventService.createEvent(entity, EventType.CREATE)
        } catch (e: Exception) {
            logger.warn("Error while create resume, message=${e.message}", e)
            throw ResumeConflictException("Conflict on create new resume")
        }

        return resumeMapper.toResponse(entity)
    }

    /**
     * Search existing resume by resume`s identifier
     * @param id resume`s identifier
     * @throws ResumeNotFoundException if resume does not exist
     * @return [ResumeResponse] if resume exists in store.
     */
    suspend fun getResumeById(id: UUID): ResumeResponse {
        logger.debug { "Request to search resume by id, $id" }

        val resume = resumeRepository.findById(id) ?:
            throw ResumeNotFoundException("Resume not found")

        return resumeMapper.toResponse(
            resume,
            resumeEducationService.search(id),
            resumeExperienceService.search(id),
            resumeSkillService.search(id)
        )
    }

    suspend fun getAllResumes(
        page: Int,
        size: Int,
        sortBy: String,
        direction: String,
        isActive: Boolean?
    ): ResponseEntity<ApiResponse<Page<ResumeResponse>>> {
        TODO()
    }

    suspend fun searchResumes(request: ResumeSearchRequest):
            ResponseEntity<ApiResponse<Page<ResumeBriefResponse>>> {
        TODO()
    }

    /**
     * Update existing resume data or throw NOT_FOUND error.
     * @param id resume`s identifier
     * @param request data for update existing resume
     * @return [ResumeResponse] updated resume data
     */
    @Transactional
    suspend fun updateResume(id: UUID, request: ResumeRequest): ResumeResponse {
        try {
            val resume = resumeRepository.findById(id) ?: throw ResumeNotFoundException("Resume not found")

            val entity = resumeMapper.toEntity(request)
            entity.id = resume.id
            val savedEntity = resumeRepository.save(entity)

            deleteAllSubEntities(id)

            resumeEducationService.create(id, request.educations)
            resumeSkillService.create(id, request.skills)
            resumeExperienceService.create(id, request.experiences)

            resumeOutboxEventService.createEvent(entity, EventType.UPDATE)

            return resumeMapper.toResponse(savedEntity)
        } catch (e: Exception) {
            logger.warn("Error while update resume, message=${e.message}", e)
            throw ResumeConflictException("Conflict on create update resume")
        }
    }

    @Transactional
    suspend fun deleteResume(id: UUID) {
        logger.debug { "Request to delete resume by id, $id" }

        val entity = resumeRepository.findById(id)
            ?: throw ResumeNotFoundException("Resume not found")

        deleteAllSubEntities(id)
        resumeRepository.delete(entity)
        resumeOutboxEventService.createEvent(entity, EventType.DELETE)
    }

    /**
     * Remove all sub entities for resume.
     */
    suspend fun deleteAllSubEntities(resumeID: UUID) {
        logger.debug { "Request to delete all sub entities by $resumeID" }
        resumeSkillService.delete(resumeID)
        resumeEducationService.delete(resumeID)
        resumeExperienceService.delete(resumeID)
    }
}