/*
 * Copyright (c) 12-12/28/22, 11:36 PM.
 * Created by https://github.com/alwayswanna
 *
 */

package a.gleb.fellowworkerservice.service

import a.gleb.apicommon.fellowworker.model.request.vacancy.VacancyApiModel
import a.gleb.apicommon.fellowworker.model.request.vacancy.VacancyApiModel.TypeOfWorkPlacement
import a.gleb.apicommon.fellowworker.model.request.vacancy.VacancyApiModel.TypeOfWorkPlacement.OFFICE
import a.gleb.apicommon.fellowworker.model.request.vacancy.VacancyApiModel.TypeOfWorkPlacement.REMOTE
import a.gleb.apicommon.fellowworker.model.response.FellowWorkerResponseModel
import a.gleb.fellowworkerservice.db.dao.TypeOfWork
import a.gleb.fellowworkerservice.db.dao.TypeOfWork.FULL_EMPLOYMENT
import a.gleb.fellowworkerservice.db.dao.TypeOfWork.PART_TIME_EMPLOYMENT
import a.gleb.fellowworkerservice.db.repository.VacancyRepository
import a.gleb.fellowworkerservice.exception.InvalidUserDataException
import a.gleb.fellowworkerservice.exception.UnexpectedErrorException
import a.gleb.fellowworkerservice.mapper.VacancyModelMapper
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class VacancyService(
    private val vacancyModelMapper: VacancyModelMapper,
    private val oauth2SecurityService: Oauth2SecurityService,
    private val vacancyRepository: VacancyRepository
) {

    companion object {
        const val NOT_FOUND_VACANCY = "Данной вакансии не существует."
        const val NOT_FOUND_QUERY = "По вашему запросу не обнаружено вакансий."
        const val UNEXPECTED_ERROR = "Ошибка при получении поиске вакансий, попробуйте повторить попытку позже"
    }

    /**
     * Method create new vacancy from request data [VacancyApiModel].
     */
    suspend fun create(request: VacancyApiModel): FellowWorkerResponseModel {
        val userId = oauth2SecurityService.extractOauth2UserId()

        val vacancy = vacancyModelMapper.toVacancyEntity(UUID.fromString(userId), request)

        try {
            return vacancyModelMapper.toFellowWorkerResponseModel(
                vacancyRepository.save(vacancy),
                "Ваша вакансия успешно создана."
            )
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                HttpStatus.BAD_GATEWAY,
                "Ошибка при создании вакансии, попробуйте повторить попытку позже"
            )
        }
    }

    /**
     * Method edit existing vacancy from request data [VacancyApiModel].
     */
    suspend fun edit(request: VacancyApiModel): FellowWorkerResponseModel {
        if (request.vacancyId == null) {
            throw InvalidUserDataException(HttpStatus.BAD_REQUEST, NOT_FOUND_RESUME)
        }

        val entityModel = vacancyRepository.findVacancyById(request.vacancyId) ?: throw InvalidUserDataException(
            HttpStatus.BAD_REQUEST,
            NOT_FOUND_VACANCY
        )

        if (!oauth2SecurityService.extractOauth2UserId().equals(entityModel.ownerId)) {
            throw InvalidUserDataException(
                HttpStatus.FORBIDDEN,
                "Вы не можете редактировать вакансию другого пользователя"
            )
        }


        val vacancyToSave = vacancyModelMapper.toVacancyEntity(entityModel.ownerId, request)
        vacancyToSave.id = entityModel.id

        try {
            return vacancyModelMapper.toFellowWorkerResponseModel(
                vacancyRepository.save(vacancyToSave),
                "Ваша вакансия успешно отредактирована."
            )
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                HttpStatus.BAD_GATEWAY,
                "Ошибка при редактировании вакансии, попробуйте повторить попытку позже"
            )
        }

    }

    /**
     * Method returns vacancy by id [UUID] from request data.
     */
    suspend fun getVacancyById(vacancyId: UUID): FellowWorkerResponseModel {
        val vacancy = vacancyRepository.findVacancyById(vacancyId) ?: throw InvalidUserDataException(
            HttpStatus.BAD_REQUEST,
            NOT_FOUND_VACANCY
        )

        try {
            return vacancyModelMapper.toFellowWorkerResponseModel(vacancy, null)
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                HttpStatus.BAD_GATEWAY,
                "Ошибка при получении вакансии, попробуйте повторить попытку позже"
            )
        }
    }

    /**
     * Method delete existing vacancy by id [UUID] from request.
     */
    suspend fun deleteVacancy(vacancyId: UUID): FellowWorkerResponseModel {
        val userId = oauth2SecurityService.extractOauth2UserId()

        val vacancy = vacancyRepository.findVacancyById(vacancyId) ?: throw InvalidUserDataException(
            HttpStatus.BAD_REQUEST,
            NOT_FOUND_VACANCY
        )

        if (!userId.equals(vacancy.ownerId)) {
            throw InvalidUserDataException(
                HttpStatus.FORBIDDEN,
                "Вы не можете удалить вакансию другого пользователя."
            )
        }

        try {
            vacancyRepository.delete(vacancy)
            return FellowWorkerResponseModel().apply {
                message = "Ваша вакансия успешно удалена."
            }
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                HttpStatus.BAD_GATEWAY,
                "Ошибка при удалении вакансии, попробуйте повторить попытку позже."
            )
        }
    }

    /**
     * Method returns all vacancies.
     */
    suspend fun getAllVacancy(): FellowWorkerResponseModel {
        try {
            return vacancyModelMapper.toVacancyResponseFromFlow(vacancyRepository.findAll())
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                HttpStatus.BAD_GATEWAY,
                "Ошибка при получении всех вакансий, попробуйте повторить попытку позже"
            )
        }
    }

    /**
     * Method returns all vacancies which contains key skill.
     * @param skill key skill for query in database.
     */
    suspend fun findVacancyByKeySkills(skill: String): FellowWorkerResponseModel {
        try {
            val vacanciesInDb = vacancyRepository.findVacancyByKeySkillsContains(skill)
            if (vacanciesInDb.isEmpty()) {
                return FellowWorkerResponseModel()
                    .apply {
                        message = NOT_FOUND_QUERY
                    }
            }
            return vacancyModelMapper.toVacancyResponseFromList(vacanciesInDb)
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                HttpStatus.BAD_GATEWAY,
                UNEXPECTED_ERROR
            )
        }
    }

    /**
     * Method returns all vacancies which contains type.
     * @param typeOfWorkPlacement key skill for query in database.
     */
    suspend fun findVacancyByTypePlacement(typeOfWorkPlacement: String): FellowWorkerResponseModel {
        val request = mapTypeOfWorkPlacement(typeOfWorkPlacement) ?: throw InvalidUserDataException(
            HttpStatus.BAD_REQUEST,
            NOT_FOUND_VACANCY
        )

        try {
            val vacanciesInDb = vacancyRepository.findVacancyByTypeOfWorkPlacement(request)
            if (vacanciesInDb.isEmpty()) {
                return FellowWorkerResponseModel()
                    .apply {
                        message = NOT_FOUND_QUERY
                    }
            }
            return vacancyModelMapper.toVacancyResponseFromList(vacanciesInDb)
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                HttpStatus.BAD_GATEWAY,
                UNEXPECTED_ERROR
            )
        }
    }

    /**
     * Method returns all vacancies which contains city.
     * @param city city for query in database.
     */
    suspend fun findVacancyByCity(city: String): FellowWorkerResponseModel {
        try {
            val vacanciesInDb = vacancyRepository.findVacancyByCityName(city)
            if (vacanciesInDb.isEmpty()) {
                return FellowWorkerResponseModel()
                    .apply {
                        message = NOT_FOUND_QUERY
                    }
            }
            return vacancyModelMapper.toVacancyResponseFromList(vacanciesInDb)
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                HttpStatus.BAD_GATEWAY,
                UNEXPECTED_ERROR
            )
        }
    }

    /**
     * Method returns all vacancies which contains type.
     * @param type for query in database.
     */
    suspend fun findVacancyByType(type: String): FellowWorkerResponseModel {
        val request = mapTypeOfWork(type) ?: throw InvalidUserDataException(
            HttpStatus.BAD_REQUEST,
            NOT_FOUND_VACANCY
        )

        try {
            val vacanciesInDb = vacancyRepository.findVacancyByTypeWork(request)
            if (vacanciesInDb.isEmpty()) {
                return FellowWorkerResponseModel()
                    .apply {
                        message = NOT_FOUND_QUERY
                    }
            }
            return vacancyModelMapper.toVacancyResponseFromList(vacanciesInDb)
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                HttpStatus.BAD_GATEWAY,
                UNEXPECTED_ERROR
            )
        }
    }

    /**
     * Method mapper.
     * @param type data from request.
     */
    suspend fun mapTypeOfWork(type: String): TypeOfWork? {
        if (FULL_EMPLOYMENT.name == type) {
            return FULL_EMPLOYMENT
        } else if (PART_TIME_EMPLOYMENT.name == type) {
            return PART_TIME_EMPLOYMENT
        }

        return null
    }

    /**
     * Method mapper.
     * @param type data from request.
     */
    suspend fun mapTypeOfWorkPlacement(type: String): TypeOfWorkPlacement? {
        if (OFFICE.name == type) {
            return OFFICE
        } else if (REMOTE.name == type) {
            return REMOTE
        }

        return null
    }
}