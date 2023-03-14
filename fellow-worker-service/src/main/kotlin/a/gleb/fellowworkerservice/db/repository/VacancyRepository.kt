/*
 * Copyright (c) 12-3/1/23, 12:14 AM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.db.repository

import a.gleb.fellowworkerservice.db.dao.TypeOfWork
import a.gleb.fellowworkerservice.db.dao.TypePlacement
import a.gleb.fellowworkerservice.db.dao.Vacancy
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VacancyRepository : CoroutineCrudRepository<Vacancy, UUID> {

    suspend fun findVacancyById(id: UUID): Vacancy?

    suspend fun findVacancyByCityName(city: String): List<Vacancy>

    suspend fun findVacancyByTypeWork(type: TypeOfWork): List<Vacancy>

    suspend fun findVacancyByTypeOfWorkPlacement(typeOfWork: TypePlacement): List<Vacancy>

    suspend fun findVacancyByKeySkillsContains(skill: String): List<Vacancy>

    suspend fun findAllByOwnerId(id: UUID): List<Vacancy>
}