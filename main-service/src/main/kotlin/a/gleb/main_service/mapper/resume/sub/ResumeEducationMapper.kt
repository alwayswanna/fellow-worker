package a.gleb.main_service.mapper.resume.sub

import a.gleb.apicommon.api.main_service.EducationDto
import a.gleb.main_service.db.entity.resume.sub.ResumeEducation
import org.springframework.stereotype.Component

@Component
class ResumeEducationMapper {

    fun toEntity(request: EducationDto): ResumeEducation {
        return ResumeEducation(
            institution = request.institution,
            degree = request.degree,
            fieldOfStudy = request.fieldOfStudy,
            startDate = request.startDate,
            endDate = request.endDate,
            isCurrent = request.isCurrent,
            description = request.description,
            resumeId = null
        )
    }

    suspend fun toResponse(entity: ResumeEducation): EducationDto {
        return EducationDto.builder()
            .institution(entity.institution)
            .degree(entity.degree)
            .fieldOfStudy(entity.fieldOfStudy)
            .startDate(entity.startDate)
            .endDate(entity.endDate)
            .isCurrent(entity.isCurrent)
            .description(entity.description)
            .build()
    }
}