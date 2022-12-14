/*
 * Copyright (c) 12-12/28/22, 11:14 PM.
 * Created by https://github.com/alwayswanna
 *
 */

package a.gleb.fellowworkerservice.mapper

import a.gleb.apicommon.fellowworker.model.request.vacancy.ContactApiModel
import a.gleb.apicommon.fellowworker.model.request.vacancy.VacancyApiModel
import a.gleb.apicommon.fellowworker.model.request.vacancy.VacancyApiModel.TypeOfWorkPlacement.OFFICE
import a.gleb.apicommon.fellowworker.model.response.FellowWorkerResponseModel
import a.gleb.apicommon.fellowworker.model.response.resume.ResumeResponseModel
import a.gleb.apicommon.fellowworker.model.response.vacancy.VacancyResponseApiModel
import a.gleb.fellowworkerservice.db.dao.TypeOfWork
import a.gleb.fellowworkerservice.db.dao.TypeOfWork.FULL_EMPLOYMENT
import a.gleb.fellowworkerservice.db.dao.TypeOfWork.PART_TIME_EMPLOYMENT
import a.gleb.fellowworkerservice.db.dao.TypePlacement
import a.gleb.fellowworkerservice.db.dao.Vacancy
import a.gleb.fellowworkerservice.db.dao.VacancyContactInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

/**
 * Mapper component.
 * [VacancyModelMapper] convert database entities to ..
 *
 */
@Component
class VacancyModelMapper {

    /**
     * Convert request data to entity [Vacancy]. From authorized request.
     * @param userId from JWT token. (OAuth2)
     * @param request data from request.
     */
    suspend fun toVacancyEntity(userId: UUID, request: VacancyApiModel): Vacancy {
        return Vacancy(
            id = UUID.randomUUID(),
            ownerId = userId,
            typeWork = convertTypeWork(request),
            typeOfWorkPlacement = covertTypeWorkPlacement(request),
            companyName = request.companyName,
            companyFullAddress = request.companyFullAddress,
            keySkills = request.keySkills,
            cityName = request.cityName,
            workingResponsibilities = request.workingResponsibilities,
            companyBonuses = request.companyBonuses,
            companyContact = convertContact(request),
            vacancyName = request.vacancyName,
            lastUpdate = LocalDateTime.now()
        )
    }

    /**
     * Method convert entity model [Vacancy] to response model [FellowWorkerResponseModel]
     * @param messageToUser message in response.
     * @param entity model from database.
     */
    suspend fun toFellowWorkerResponseModel(entity: Vacancy, messageToUser: String?): FellowWorkerResponseModel {
        return FellowWorkerResponseModel().apply {
            message = messageToUser
            vacancyResponse = toVacancyResponse(entity)
        }
    }

    /**
     * Method convert [Flow] of entities from database to response model [FellowWorkerResponseModel].
     * @param allVacancies entities from database.
     */
    suspend fun toVacancyResponseFromFlow(allVacancies: Flow<Vacancy>): FellowWorkerResponseModel {
        val vacancyCollection = allVacancies.map { toVacancyResponse(it) }.toList()
        return FellowWorkerResponseModel()
            .apply {
                vacancies = vacancyCollection
            }
    }

    /**
     * Method convert [List] of entities from database to response model [FellowWorkerResponseModel].
     * @param allVacancies entities from database.
     */
    suspend fun toVacancyResponseFromList(allVacancies: List<Vacancy>): FellowWorkerResponseModel {
        val vacancyCollection = allVacancies.map { toVacancyResponse(it) }.toList()
        return FellowWorkerResponseModel()
            .apply {
                vacancies = vacancyCollection
            }
    }

    /**
     * Method convert entity model [Vacancy] to part of model [ResumeResponseModel] response.
     * @param entity model from database.
     */
    fun toVacancyResponse(entity: Vacancy): VacancyResponseApiModel {
        return VacancyResponseApiModel().apply {
            resumeId = entity.id
            vacancyName = entity.vacancyName
            typeOfWork = entity.typeWork.name
            typeOfWorkPlacement = entity.typeOfWorkPlacement.name
            companyName = entity.companyName
            companyFullAddress = entity.companyFullAddress
            keySkills = entity.keySkills
            cityName = entity.cityName
            workingResponsibilities = entity.workingResponsibilities
            companyBonuses = entity.companyBonuses
            contactApiModel = toCompanyContact(entity)
            lastUpdate = entity.lastUpdate
        }
    }

    /**
     * Method convert entity contact info about company [VacancyContactInfo] to model for response
     * [ContactApiModel]
     */
    private fun toCompanyContact(entity: Vacancy): ContactApiModel {
        return ContactApiModel().apply {
            fio = entity.companyContact.fio
            phone = entity.companyContact.phone
            email = entity.companyContact.email
        }
    }

    /**
     * Method covert [VacancyApiModel] to entity model [VacancyContactInfo].
     * @param request contact data from request.
     */
    private fun convertContact(request: VacancyApiModel): VacancyContactInfo {
        val contactInfo = request.contactApiModel
        return VacancyContactInfo(
            fio = contactInfo.fio,
            phone = contactInfo.phone,
            email = contactInfo.email
        )
    }

    /**
     * Method convert [VacancyApiModel] to entity model entity [TypeOfWork].
     * @param request data from request.
     */
    private fun convertTypeWork(request: VacancyApiModel): TypeOfWork {
        return if (request.typeOfWork.name == FULL_EMPLOYMENT.name) {
            FULL_EMPLOYMENT
        } else {
            PART_TIME_EMPLOYMENT
        }
    }

    /**
     * Method convert [VacancyApiModel] to entity model entity [TypePlacement].
     * @param request data from request.
     */
    private fun covertTypeWorkPlacement(request: VacancyApiModel): TypePlacement {
        return if (request.typeOfWorkPlacement.name == OFFICE.name) {
            TypePlacement.OFFICE
        } else {
            TypePlacement.REMOTE
        }
    }
}