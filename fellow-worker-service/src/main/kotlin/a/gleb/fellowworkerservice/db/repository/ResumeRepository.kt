/*
 * Copyright (c) 07-1/26/23, 11:40 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.db.repository

import a.gleb.fellowworkerservice.db.dao.Resume
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ResumeRepository : CoroutineCrudRepository<Resume, UUID> {

    suspend fun findResumeById(id: UUID): Resume?

    suspend fun findAllByOwnerRecordId(id: UUID): Resume?
}