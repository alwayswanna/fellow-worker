/*
 * Copyright (c) 12-3/7/23, 10:25 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.service

import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import a.gleb.apicommon.fellowworker.model.request.resume.SearchResumeApiModel
import a.gleb.apicommon.fellowworker.model.response.FellowWorkerResponseModel
import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageCreate
import a.gleb.apicommon.fellowworker.model.rmq.ResumeMessageDelete
import a.gleb.fellowworkerservice.db.dao.Resume
import a.gleb.fellowworkerservice.db.repository.ResumeRepository
import a.gleb.fellowworkerservice.exception.InvalidUserDataException
import a.gleb.fellowworkerservice.exception.UnexpectedErrorException
import a.gleb.fellowworkerservice.mapper.ResumeModelMapper
import kotlinx.coroutines.flow.asFlow
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_GATEWAY
import org.springframework.stereotype.Service
import java.util.*

const val NOT_FOUND_RESUME = "Данного резюме не существует."
private val logger = KotlinLogging.logger { }

@Service
class ResumeService(
    private val resumeRepository: ResumeRepository,
    private val oauth2SecurityService: Oauth2SecurityService,
    private val resumeModelMapper: ResumeModelMapper,
    private val resumeSenderService: ResumeSenderService
) {

    /**
     * Method create new resume from request data [ResumeApiModel].
     */
    suspend fun createResume(request: ResumeApiModel): FellowWorkerResponseModel {
        val userId = oauth2SecurityService.extractOauth2UserId()

        val existingResume = resumeRepository.findByOwnerRecordId(UUID.fromString(userId))
        if (existingResume != null) {
            logger.info {
                "Current user: $userId already have resume (resumeId: ${existingResume.id}), deleting existing resume"
            }
            resumeRepository.delete(existingResume)
            resumeSenderService.sendMessageRemove(ResumeMessageDelete(existingResume.id))
        }

        val resume = resumeModelMapper.toResumeDtoModel(UUID.fromString(userId), request, null)

        try {
            val savedResume = resumeRepository.save(resume)
            val response = resumeModelMapper.toFellowWorkerResponseModel(
                savedResume,
                "Ваше резюме успешно опубликовано. PDF версия станет доступна чуть позже."
            )
            resumeSenderService.sendMessageCreate(
                ResumeMessageCreate(
                    response.resumeResponse,
                    request.base64Image,
                    request.extensionPostfix
                )
            )
            return response
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                BAD_GATEWAY,
                "Ошибка при создании резюме, попробуйте повторить попытку позже"
            )
        }
    }

    /**
     * Method edit existing resume from request data [ResumeApiModel].
     */
    suspend fun editResume(request: ResumeApiModel): FellowWorkerResponseModel {
        if (request.resumeId == null) {
            throw InvalidUserDataException(HttpStatus.BAD_REQUEST, NOT_FOUND_RESUME)
        }

        val resumeModel = resumeRepository.findResumeById(request.resumeId)
            ?: throw InvalidUserDataException(HttpStatus.BAD_REQUEST, NOT_FOUND_RESUME)

        if (oauth2SecurityService.extractOauth2UserId() != resumeModel.ownerRecordId.toString()) {
            throw InvalidUserDataException(
                HttpStatus.FORBIDDEN,
                "Вы не можете редактировать резюме другого пользователя"
            )
        }

        val resumeToSave = resumeModelMapper.toResumeDtoModel(resumeModel.ownerRecordId, request, request.resumeId)

        try {
            resumeSenderService.sendMessageRemove(ResumeMessageDelete(request.resumeId))
            val response = resumeModelMapper.toFellowWorkerResponseModel(
                resumeRepository.save(resumeToSave),
                "Ваше резюме успешно обновлено. PDF версия станет доступна чуть позже."
            )
            resumeSenderService.sendMessageCreate(
                ResumeMessageCreate(
                    response.resumeResponse,
                    request.base64Image,
                    request.extensionPostfix
                )
            )
            return response
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                BAD_GATEWAY,
                "Ошибка при редактировании резюме, попробуйте повторить попытку позже"
            )
        }
    }

    /**
     * Method delete existing resume by id [UUID] from request.
     */
    suspend fun deleteResume(id: UUID): FellowWorkerResponseModel {
        val userId = oauth2SecurityService.extractOauth2UserId()

        val resume = resumeRepository.findResumeById(id) ?: throw InvalidUserDataException(
            HttpStatus.BAD_REQUEST,
            NOT_FOUND_RESUME
        )

        if (userId != resume.ownerRecordId.toString()) {
            throw InvalidUserDataException(
                HttpStatus.FORBIDDEN,
                "Вы не можете удалить резюме другого пользователя"
            )
        }

        try {
            resumeSenderService.sendMessageRemove(ResumeMessageDelete(id))
            resumeRepository.delete(resume)
            return FellowWorkerResponseModel().apply {
                message = "Ваше резюме успешно удалено"
            }
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                BAD_GATEWAY,
                "Ошибка при удалении резюме, попробуйте повторить попытку позже"
            )
        }
    }

    /**
     * Method return resume by id [UUID] from request data.
     */
    suspend fun findResumeModelById(id: UUID): FellowWorkerResponseModel {
        val resume = resumeRepository.findResumeById(id) ?: throw InvalidUserDataException(
            HttpStatus.BAD_REQUEST,
            NOT_FOUND_RESUME
        )

        try {
            return resumeModelMapper.toFellowWorkerResponseModel(resume, null)
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                BAD_GATEWAY,
                "Ошибка при получении резюме, попробуйте повторить попытку позже"
            )
        }
    }

    /**
     * Returns resume for current user.
     */
    suspend fun getCurrentUserResume(): FellowWorkerResponseModel {
        val userId = oauth2SecurityService.extractOauth2UserId()

        try {
            val resume = resumeRepository.findByOwnerRecordId(UUID.fromString(userId))
                ?: return FellowWorkerResponseModel()
                    .apply {
                        message = "Вы еще не создавали резюме"
                    }

            return resumeModelMapper.toFellowWorkerResponseModel(resume, "С вашим аккаунтом связаны следующие резюме")

        } catch (e: Exception) {
            throw UnexpectedErrorException(
                BAD_GATEWAY,
                "Ошибка при получении резюме, попробуйте повторить попытку позже"
            )
        }
    }

    /**
     * Method returns all resume.
     */
    suspend fun allResume(): FellowWorkerResponseModel {
        try {
            val response = resumeModelMapper.toResumeResponseFlow(resumeRepository.findAll())
            response.apply {
                message = "Все найденные резюме"
            }
            return response
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                BAD_GATEWAY,
                "Ошибка при получении всех резюме, попробуйте повторить попытку позже"
            )
        }
    }

    /**
     * Method find resumes with specific option.
     * @param request object with filter option
     */
    suspend fun filter(request: SearchResumeApiModel): FellowWorkerResponseModel {
        try {
            val result = mutableListOf<Resume>()

            with(request) {
                if (!city.isNullOrBlank()) {
                    result.addAll(resumeRepository.findAllByCity(city))
                }

                if (!keySkills.isNullOrBlank()) {
                    result.addAll(resumeRepository.findAllByProfessionalSkillsContains(keySkills))
                }

                if (!job.isNullOrBlank()) {
                    result.addAll(resumeRepository.findAllByJobContains(job))
                }

                if (!salary.isNullOrBlank()) {
                    result.addAll(resumeRepository.findAllByExpectedSalaryBetween(0, salary.toInt()))
                }

                val identicalVacancies = result.asSequence().map { it.id }.toSet()

                val finalResult = result.filter { identicalVacancies.contains(it.id) }.asFlow()

                val resumeResponse = resumeModelMapper.toResumeResponseFlow(finalResult)
                resumeResponse.apply {
                    message = "Вот что мы нашли"
                }

                return resumeResponse
            }
        } catch (e: Exception) {
            throw UnexpectedErrorException(
                BAD_GATEWAY,
                "Ошибка при фильтрации попробуйте повторить попытку позже"
            )
        }
    }
}