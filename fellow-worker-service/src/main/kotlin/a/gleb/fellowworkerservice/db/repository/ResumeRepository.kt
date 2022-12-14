package a.gleb.fellowworkerservice.db.repository

import a.gleb.fellowworkerservice.db.dao.Resume
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ResumeRepository : CoroutineCrudRepository<Resume, UUID> {

    suspend fun findResumeById(id: UUID): Resume?

    suspend fun findAllByOwnerRecordId(id: UUID): List<Resume>?
}