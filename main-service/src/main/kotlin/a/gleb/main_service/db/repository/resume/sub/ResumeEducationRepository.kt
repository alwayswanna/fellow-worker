package a.gleb.main_service.db.repository.resume.sub

import a.gleb.main_service.db.entity.resume.sub.ResumeEducation
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface ResumeEducationRepository : CoroutineCrudRepository<ResumeEducation, UUID> {

    suspend fun findAllByResumeId(resumeId: UUID): MutableList<ResumeEducation>

    suspend fun deleteByResumeId(resumeId: UUID)
}