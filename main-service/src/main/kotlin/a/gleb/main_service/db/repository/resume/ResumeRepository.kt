package a.gleb.main_service.db.repository.resume

import a.gleb.main_service.db.entity.resume.Resume
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.*

interface ResumeRepository : CoroutineCrudRepository<Resume, UUID> {
}