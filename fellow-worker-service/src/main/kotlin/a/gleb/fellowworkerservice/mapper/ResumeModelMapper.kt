/*
 * Copyright (c) 12-3/12/23, 1:00 PM
 * Created by https://github.com/alwayswanna
 */

package a.gleb.fellowworkerservice.mapper

import a.gleb.apicommon.fellowworker.model.request.resume.EducationApiModel
import a.gleb.apicommon.fellowworker.model.request.resume.ResumeApiModel
import a.gleb.apicommon.fellowworker.model.response.FellowWorkerResponseModel
import a.gleb.apicommon.fellowworker.model.response.resume.ContactResponseModel
import a.gleb.apicommon.fellowworker.model.response.resume.EducationResponseModel
import a.gleb.apicommon.fellowworker.model.response.resume.ResumeResponseModel
import a.gleb.apicommon.fellowworker.model.response.resume.WorkExperienceResponseModel
import a.gleb.fellowworkerservice.db.dao.*
import a.gleb.fellowworkerservice.db.dao.EducationLevel.*
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
                city = resume.city
                job = resume.job
                expectedSalary = resume.expectedSalary.toString()
                about = resume.about
                education = resume.education.asSequence().map {
                    EducationResponseModel().apply {
                        startTime = it.startTime
                        endTime = it.endTime
                        educationalInstitution = it.educationalInstitution
                        educationLevel = determineResumeResponseEducationLevel(it.educationLevel)
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
                                workingSpeciality = it.workingSpecialty
                                responsibilities = it.responsibilities
                            }
                    }
                    .toList()
                contact = ContactResponseModel().apply {
                    phone = resume.contact.phone
                    email = resume.contact.email
                }
                lastUpdate = resume.lastUpdate
            }
    }

    private fun determineResumeResponseEducationLevel(educationLevel: EducationLevel): EducationResponseModel.EducationLevel {
        return if (BACHELOR.toString() == educationLevel.toString()) {
            EducationResponseModel.EducationLevel.BACHELOR
        } else if (MAGISTRACY.toString() == educationLevel.toString()) {
            EducationResponseModel.EducationLevel.MAGISTRACY
        } else {
            EducationResponseModel.EducationLevel.SPECIALTY
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
    suspend fun toResumeDtoModel(userId: UUID, request: ResumeApiModel, resumeId: UUID?): Resume {
        val salary = if (request.expectedSalary.isNullOrEmpty()){ null } else request.expectedSalary.toInt()

        return Resume(
            id = resumeId ?: UUID.randomUUID(),
            ownerRecordId = userId,
            firstName = request.firstName,
            middleName = request.middleName,
            lastName = request.lastName,
            birthDate = request.birthDate,
            job = request.job,
            expectedSalary = salary,
            about = request.about ?: "",
            city = request.city,
            education = toEducationDtoModel(request),
            professionalSkills = request.professionalSkills,
            workHistory = toWorkHistoryDto(request),
            contact = ContactModel(request.contact.phone, request.contact.email),
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
                    educationLevel = determineEducationLevelFromString(it.educationLevel)
                )
            }
            .toList()
    }

    /**
     * Method map education level.
     */
    private fun determineEducationLevelFromString(level: EducationApiModel.EducationLevel): EducationLevel {
        return if (SPECIALTY.toString() == level.toString()) {
            SPECIALTY
        } else if (BACHELOR.toString() == level.toString()) {
            BACHELOR
        } else {
            MAGISTRACY
        }
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
                )
            }
            .toList()
    }
}
