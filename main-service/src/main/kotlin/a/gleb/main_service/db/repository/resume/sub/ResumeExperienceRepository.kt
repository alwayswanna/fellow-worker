package a.gleb.main_service.db.repository.resume.sub

import a.gleb.main_service.db.entity.resume.sub.ResumeExperience
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface ResumeExperienceRepository : CoroutineCrudRepository<ResumeExperience, UUID> {

    suspend fun findAllByResumeId(resumeId: UUID): MutableList<ResumeExperience>

    suspend fun deleteByResumeId(resumeId: UUID)
}