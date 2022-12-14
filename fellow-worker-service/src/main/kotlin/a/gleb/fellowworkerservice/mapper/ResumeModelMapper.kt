/*
 * Copyright (c) 12-12/24/22, 10:24 PM.
 * Created by https://github.com/alwayswanna
 *
 */

package a.gleb.fellowworkerservice.mapper

import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import a.gleb.apicommon.fellowworker.model.response.FellowWorkerResponseModel
import a.gleb.apicommon.fellowworker.model.response.resume.EducationResponseModel
import a.gleb.apicommon.fellowworker.model.response.resume.ResumeResponseModel
import a.gleb.apicommon.fellowworker.model.response.resume.WorkExperienceResponseModel
import a.gleb.fellowworkerservice.db.dao.Education
import a.gleb.fellowworkerservice.db.dao.Resume
import a.gleb.fellowworkerservice.db.dao.WorkExperience
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*


/**
 * Mapper component.
 * [ResumeModelMapper] convert database entities to response models [FellowWorkerResponseModel] & [ResumeModelMapper],
 * and convert request data models [ResumeApiModel] to entity.
 *
 */
@Component
class ResumeModelMapper {

    /**
     * Method convert [Resume] entity to response [FellowWorkerResponseModel] with message.
     * @param resume entity from database.
     * @param messageToUser message for user.
     */
    suspend fun toFellowWorkerResponseModel(resume: Resume, messageToUser: String?): FellowWorkerResponseModel {
        return FellowWorkerResponseModel()
            .apply {
                message = messageToUser
                resumeResponse = toResumeResponse(resume)
            }
    }

    /**
     * Method convert list of entities [Resume] from database to response [FellowWorkerResponseModel].
     * @param resumeList collection with resume from database.
     */
    suspend fun toResponseWithListOfResume(resumeList: List<Resume>): FellowWorkerResponseModel {
        return FellowWorkerResponseModel()
            .apply {
                resumes = resumeList.asSequence()
                    .map { toResumeResponse(it) }
                    .toList()
            }
    }

    /**
     * Method convert entity from database [Resume] to part of service response [ResumeResponseModel]
     * @param resume entity from database.
     */
    fun toResumeResponse(resume: Resume): ResumeResponseModel {
        return ResumeResponseModel()
            .apply {
                resumeId = resume.id
                userId = resume.ownerRecordId
                firstName = resume.firstName
                middleName = resume.middleName
                lastName = resume.lastName
                birthDate = resume.birthDate
                job = resume.job
                expectedSalary = resume.expectedSalary.toString()
                about = resume.about
                education = resume.education.asSequence().map {
                    EducationResponseModel().apply {
                        startTime = it.startTime
                        endTime = it.endTime
                        educationalInstitution = it.educationalInstitution
                        educationLevel = it.educationLevel
                    }
                }.toList()
                professionalSkills = resume.professionalSkills
                workingHistory = resume.workHistory.asSequence()
                    .map {
                        WorkExperienceResponseModel()
                            .apply {
                                startTime = it.startTime
                                endTime = it.endTime
                                companyName = it.companyName
                                workingSpecialty = it.workingSpecialty
                                responsibilities = it.responsibilities
                                tags = it.tags
                            }
                    }
                    .toList()
                lastUpdate = resume.lastUpdate
            }
    }

    /**
     * Method convert [Flow] of entities from database to response model [FellowWorkerResponseModel].
     * @param allResume entities from database.
     */
    suspend fun toResumeResponseFlow(allResume: Flow<Resume>): FellowWorkerResponseModel {
        val resumeInCollection = allResume.map { toResumeResponse(it) }.toList()
        return FellowWorkerResponseModel()
            .apply {
                resumes = resumeInCollection
            }
    }

    /**
     * Method convert request [ResumeApiModel] with data for resume to entity [Resume].
     * @param request request which send authorized user.
     * @param userId from JWT token.
     */
    suspend fun toResumeDtoModel(userId: UUID, request: ResumeApiModel): Resume {
        return Resume(
            id = UUID.randomUUID(),
            ownerRecordId = userId,
            firstName = request.firstName,
            middleName = request.middleName,
            lastName = request.lastName,
            birthDate = request.birthDate,
            job = request.job,
            expectedSalary = request.expectedSalary,
            about = request.about,
            education = toEducationDtoModel(request),
            professionalSkills = request.professionalSkills,
            workHistory = toWorkHistoryDto(request),
            lastUpdate = LocalDateTime.now()
        )
    }

    /**
     * Method convert education data from request [ResumeApiModel] to education collection of entity [Education].
     * @param request data which send authorized user.
     */
    suspend fun toEducationDtoModel(request: ResumeApiModel): List<Education> {
        val educationApiModels = request.education
        if (educationApiModels.isNullOrEmpty()) {
            return listOf()
        }

        return educationApiModels.asSequence()
            .map {
                Education(
                    startTime = it.startTime,
                    endTime = it.endTime,
                    educationalInstitution = it.educationalInstitution,
                    educationLevel = it.educationLevel.name
                )
            }
            .toList()
    }

    /**
     * Method convert data about working history from request [ResumeApiModel] to collection of working history
     * [WorkExperience] for database.
     * @param request data which send authorized user.
     */
    suspend fun toWorkHistoryDto(request: ResumeApiModel): List<WorkExperience> {
        val experienceListRequest = request.workingHistory
        if (experienceListRequest.isNullOrEmpty()) {
            return listOf()
        }

        return experienceListRequest.asSequence()
            .map {
                WorkExperience(
                    startTime = it.startTime,
                    endTime = it.endTime,
                    companyName = it.companyName,
                    workingSpecialty = it.workingSpecialty,
                    responsibilities = it.responsibilities,
                    tags = it.tags ?: listOf()
                )
            }
            .toList()
    }
}
