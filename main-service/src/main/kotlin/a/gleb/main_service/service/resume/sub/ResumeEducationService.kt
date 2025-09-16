package a.gleb.main_service.service.resume.sub

import a.gleb.apicommon.api.main_service.EducationDto
import a.gleb.main_service.db.repository.resume.sub.ResumeEducationRepository
import a.gleb.main_service.mapper.resume.sub.ResumeEducationMapper
import org.springframework.stereotype.Service
import java.util.*

@Service
class ResumeEducationService(
    private val resumeEducationMapper: ResumeEducationMapper,
    private val resumeEducationRepository: ResumeEducationRepository,
) {

    suspend fun create(resumeID: UUID, educations: Set<EducationDto>?) {
        if (educations != null && educations.isNotEmpty()) {
            educations.forEach {
                val entity = resumeEducationMapper.toEntity(it)
                entity.resumeId = resumeID
                resumeEducationRepository.save(entity)
            }
        }
    }

    suspend fun delete(resumeID: UUID) {
        resumeEducationRepository.deleteByResumeId(resumeID)
    }

    suspend fun search(resumeID: UUID): Set<EducationDto>? {
        val entities = resumeEducationRepository.findAllByResumeId(resumeID)
        if (entities.isNotEmpty()) {
            return entities.map { resumeEducationMapper.toResponse(it) }.toSet()
        }

        return null
    }
}