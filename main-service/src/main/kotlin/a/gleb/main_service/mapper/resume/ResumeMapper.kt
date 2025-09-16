package a.gleb.main_service.mapper.resume

import a.gleb.apicommon.api.main_service.EducationDto
import a.gleb.apicommon.api.main_service.Types
import a.gleb.apicommon.api.main_service.WorkExperienceDto
import a.gleb.apicommon.api.main_service.resume.ResumeRequest
import a.gleb.apicommon.api.main_service.resume.ResumeResponse
import a.gleb.main_service.db.entity.resume.Resume
import org.springframework.stereotype.Component

@Component
class ResumeMapper {

    suspend fun toResponse(entity: Resume): ResumeResponse {
        return toResponse(entity, null, null, null)
    }

    suspend fun toResponse(
        entity: Resume,
        educations: Set<EducationDto>?,
        experiencies: Set<WorkExperienceDto>?,
        skills: Set<String>?
    ): ResumeResponse {

        var salaryCurrency: Types.Currency? = null
        if (entity.expectedSalaryCurrency != null) {
            salaryCurrency = Types.Currency.valueOf(entity.expectedSalaryCurrency!!)
        }


        return ResumeResponse.builder()
            .id(entity.id)
            .title(entity.title)
            .description(entity.description)
            .fullName(entity.fullName)
            .email(entity.email)
            .phone(entity.phone)
            .salaryExpectation(entity.salaryExpectation)
            .expectedSalaryCurrency(salaryCurrency)
            .experienceYears(entity.experienceYears)
            .experiences(experiencies)
            .skills(skills)
            .educations(educations)
            .employmentTypeEnumeration(Types.EmploymentTypeEnumeration.entries.firstOrNull { it.name == entity.employmentType })
            .workScheduleEnumeration(Types.WorkScheduleEnumeration.entries.firstOrNull { it.name == entity.workSchedule })
            .location(entity.location)
            .languages(entity.languages)
            .isActive(entity.isActive)
            .createdAt(entity.createdAt)
            .updatedAt(entity.updatedAt)
            .build()
    }

    suspend fun toEntity(request: ResumeRequest): Resume {
        return Resume(
            title = request.title,
            description = request.description,
            fullName = request.fullName,
            email = request.email,
            phone = request.phone,
            salaryExpectation = request.salaryExpectation,
            expectedSalaryCurrency = request.currencyExpectation?.name,
            experienceYears = request.experienceYears,
            employmentType = request.employmentTypeEnumeration.name,
            workSchedule = request.workScheduleEnumeration.name,
            location = request.location,
            languages = request.languages
        )
    }
}