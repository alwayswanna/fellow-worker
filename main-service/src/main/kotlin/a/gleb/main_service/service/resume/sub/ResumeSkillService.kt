package a.gleb.main_service.service.resume.sub

import a.gleb.main_service.db.repository.resume.sub.ResumeSkillRepository
import a.gleb.main_service.mapper.resume.sub.ResumeSkillMapper
import org.springframework.stereotype.Service
import java.util.*

@Service
class ResumeSkillService (
    private val resumeSkillRepository: ResumeSkillRepository,
    private val resumeSkillMapper: ResumeSkillMapper
){

    suspend fun create(resumeID: UUID, requestSkills: Set<String>?) {
        if (requestSkills != null && requestSkills.isNotEmpty()) {
            requestSkills.forEach { skill ->
                val entity = resumeSkillMapper.toEntity(skill)
                entity.resumeId = resumeID
                resumeSkillRepository.save(entity)
            }
        }
    }

    suspend fun delete(resumeID: UUID) {
        resumeSkillRepository.deleteByResumeId(resumeID)
    }

    suspend fun search(resumeID: UUID): Set<String>? {
        val entities = resumeSkillRepository.findAllByResumeId(resumeID)
        return resumeSkillMapper.toResponse(entities)
    }
}