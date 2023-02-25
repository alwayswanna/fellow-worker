/*
 * Copyright (c) 12-3/7/23, 10:06 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.service

import a.gleb.apicommon.fellowworker.model.request.vacancy.SearchVacancyApiModel
import a.gleb.apicommon.fellowworker.model.request.vacancy.TypeOfWorkPlacement
import a.gleb.apicommon.fellowworker.model.request.vacancy.VacancyApiModel
import a.gleb.apicommon.fellowworker.model.request.vacancy.WorkType
import a.gleb.apicommon.fellowworker.model.response.FellowWorkerResponseModel
import a.gleb.fellowworkerservice.db.dao.TypeOfWork
import a.gleb.fellowworkerservice.db.dao.TypeOfWork.FULL_EMPLOYMENT
import a.gleb.fellowworkerservice.db.dao.TypeOfWork.PART_TIME_EMPLOYMENT
import a.gleb.fellowworkerservice.db.dao.TypePlacement
import a.gleb.fellowworkerservice.db.dao.Vacancy
import a.gleb.fellowworkerservice.db.repository.VacancyRepository
import a.gleb.fellowworkerservice.exception.InvalidUserDataException
import a.gleb.fellowworkerservice.exception.UnexpectedErrorException
import a.gleb.fellowworkerservice.mapper.VacancyModelMapper
import kotlinx.coroutines.flow.asFlow
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.stereotype.Service
import org.springframework.util.CollectionUtils
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
                BAD_GATEWAY,
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

        if (oauth2SecurityService.extractOauth2UserId() != entityModel.ownerId.toString()) {
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
                BAD_GATEWAY,
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
                BAD_GATEWAY,
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

        if (userId != vacancy.ownerId.toString()) {
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
                BAD_GATEWAY,
                "Ошибка при удалении вакансии, попробуйте повторить попытку позже."
            )
        }
    }

    /**
     * Method returns all vacancies.
     */
    suspend fun getAllVacancy(): FellowWorkerResponseModel {
        try {
            val vacanciesResponse = vacancyModelMapper.toVacancyResponseFromFlow(vacancyRepository.findAll())
            vacanciesResponse.apply {
                message = "Все доступные вакансии"
            }
            return vacanciesResponse
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                BAD_GATEWAY,
                "Ошибка при получении всех вакансий, попробуйте повторить попытку позже"
            )
        }
    }

    /**
     * Method returns all vacancies by current user.
     */
    suspend fun getCurrentUserVacancies(): FellowWorkerResponseModel {
        val userId = oauth2SecurityService.extractOauth2UserId()

        val vacancyIterable = vacancyRepository.findAllByOwnerId(UUID.fromString(userId))
        val messageToResponse = if (CollectionUtils.isEmpty(vacancyIterable)) {
            "У вас нету активных вакансий."
        } else {
            "Ваши активные вакансии:"
        }

        return if (CollectionUtils.isEmpty(vacancyIterable)) {
            FellowWorkerResponseModel().apply {
                message = messageToResponse
            }
        } else {
            vacancyModelMapper.toVacancyResponseFromList(vacancyIterable).apply {
                message = messageToResponse
            }
        }
    }

    /**
     * Method find vacancies with specific option.
     * @param request object with filter option
     */
    suspend fun filter(request: SearchVacancyApiModel): FellowWorkerResponseModel {
        try {
            val result = mutableListOf<Vacancy>()

            with(request) {
                if (!city.isNullOrBlank()) {
                    result.addAll(vacancyRepository.findVacancyByCityName(city))
                }

                if (typeOfWorkPlacement != null) {
                    val type = mapTypeOfWorkPlacement(typeOfWorkPlacement)
                    result.addAll(vacancyRepository.findVacancyByTypeOfWorkPlacement(type))
                }

                if (typeOfWork != null) {
                    val type = mapTypeOfWork(typeOfWork)
                    result.addAll(vacancyRepository.findVacancyByTypeWork(type))
                }

                if (!keySkills.isNullOrBlank()) {
                    val partsOfKeySkills = keySkills.split(" ")
                    for (i in partsOfKeySkills) {
                        result.addAll(vacancyRepository.findVacancyByKeySkillsContains(i))
                    }
                }
            }

            val identicalVacancies = result.asSequence().map { it.id }.toSet()

            val finalResult = result.filter { identicalVacancies.contains(it.id) }.asFlow()

            val vacanciesResponse = vacancyModelMapper.toVacancyResponseFromFlow(finalResult)
            vacanciesResponse.apply {
                message = "Вот что мы нашли"
            }

            return vacanciesResponse

        } catch (e: Exception) {
            throw UnexpectedErrorException(
                BAD_GATEWAY,
                "Ошибка при фильтрации попробуйте повторить попытку позже"
            )
        }
    }

    private fun mapTypeOfWork(typeOfWork: WorkType): TypeOfWork {
        return if (FULL_EMPLOYMENT.name == typeOfWork.name) {
            return FULL_EMPLOYMENT
        } else {
            return PART_TIME_EMPLOYMENT
        }
    }

    /**
     * Method mapper.
     * @param type data from request.
     */
    suspend fun mapTypeOfWorkPlacement(type: TypeOfWorkPlacement): TypePlacement {
        return if (TypePlacement.OFFICE.name == type.name) {
            TypePlacement.OFFICE
        } else {
            TypePlacement.REMOTE
        }
    }
}