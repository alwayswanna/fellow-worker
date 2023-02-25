/*
 * Copyright (c) 07-3/7/23, 10:06 PM
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

    suspend fun findByOwnerRecordId(id: UUID): Resume?

    suspend fun findAllByCity(city: String): List<Resume>

    suspend fun findAllByProfessionalSkillsContains(skill: String): List<Resume>

    suspend fun findAllByJobContains(job: String): List<Resume>

    suspend fun findAllByExpectedSalaryBetween(start: Int, end: Int): List<Resume>
}