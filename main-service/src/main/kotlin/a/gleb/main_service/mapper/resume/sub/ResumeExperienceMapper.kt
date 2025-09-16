package a.gleb.main_service.mapper.resume.sub

import a.gleb.apicommon.api.main_service.WorkExperienceDto
import a.gleb.main_service.db.entity.resume.sub.ResumeExperience
import org.springframework.stereotype.Component

@Component
class ResumeExperienceMapper {

    fun toEntity(request: WorkExperienceDto): ResumeExperience {
        return ResumeExperience(
            company = request.company,
            position = request.position,
            startDate = request.startDate,
            endDate = request.endDate,
            isCurrent = request.isCurrent,
            description = request.description,
            resumeId = null
        )
    }

    suspend fun toResponse(entity: ResumeExperience): WorkExperienceDto {
        return WorkExperienceDto.builder()
            .company(entity.company)
            .position(entity.position)
            .startDate(entity.startDate)
            .endDate(entity.endDate)
            .isCurrent(entity.isCurrent)
            .description(entity.description)
            .build()
    }
}