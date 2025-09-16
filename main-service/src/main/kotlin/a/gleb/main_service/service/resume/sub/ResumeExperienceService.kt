package a.gleb.main_service.service.resume.sub

import a.gleb.apicommon.api.main_service.WorkExperienceDto
import a.gleb.main_service.db.repository.resume.sub.ResumeExperienceRepository
import a.gleb.main_service.mapper.resume.sub.ResumeExperienceMapper
import org.springframework.stereotype.Service
import java.util.*

@Service
class ResumeExperienceService (
    private val resumeExperienceMapper: ResumeExperienceMapper,
    private val resumeExperienceRepository: ResumeExperienceRepository,
){

    suspend fun create(resumeID: UUID, experiences: Set<WorkExperienceDto>?) {
        if (experiences != null && experiences.isNotEmpty()) {
            experiences.forEach { experience ->
                val entity = resumeExperienceMapper.toEntity(experience)
                entity.resumeId = resumeID
                resumeExperienceRepository.save(entity)
            }
        }
    }

    suspend fun delete(resumeID: UUID) {
        resumeExperienceRepository.deleteByResumeId(resumeID)
    }

    suspend fun search(resumeID: UUID) : Set<WorkExperienceDto>? {
        val entities = resumeExperienceRepository.findAllByResumeId(resumeID)
        if (entities.isNotEmpty()) {
            return entities.map { resumeExperienceMapper.toResponse(it) }.toSet()
        }

        return null
    }
}