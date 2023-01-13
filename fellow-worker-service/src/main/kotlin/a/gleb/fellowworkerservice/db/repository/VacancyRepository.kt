/*
 * Copyright (c) 12-07.01.2023, 20:21
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.db.repository

import a.gleb.apicommon.fellowworker.model.request.vacancy.VacancyApiModel.TypeOfWorkPlacement
import a.gleb.fellowworkerservice.db.dao.TypeOfWork
import a.gleb.fellowworkerservice.db.dao.Vacancy
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface VacancyRepository : CoroutineCrudRepository<Vacancy, UUID> {

    suspend fun findVacancyById(id: UUID): Vacancy?

    suspend fun findVacancyByCityName(city: String): List<Vacancy>

    suspend fun findVacancyByTypeWork(type: TypeOfWork): List<Vacancy>

    suspend fun findVacancyByTypeOfWorkPlacement(typeOfWork: TypeOfWorkPlacement): List<Vacancy>

    suspend fun findVacancyByKeySkillsContains(skill: String): List<Vacancy>
}