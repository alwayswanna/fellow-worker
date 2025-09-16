package a.gleb.main_service.db.repository.resume.sub

import a.gleb.main_service.db.entity.resume.sub.ResumeSkill
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface ResumeSkillRepository : CoroutineCrudRepository<ResumeSkill, UUID> {

    suspend fun findAllByResumeId(resumeId: UUID): MutableList<ResumeSkill>

    suspend fun deleteByResumeId(resumeId: UUID)
}