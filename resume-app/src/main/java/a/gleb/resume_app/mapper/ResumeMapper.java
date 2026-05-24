package a.gleb.resume_app.mapper;

import a.gleb.resume_app.db.entity.*;
import a.gleb.resume_app.model.request.EducationRequest;
import a.gleb.resume_app.model.request.ResumeRequest;
import a.gleb.resume_app.model.request.WorkExperienceRequest;
import a.gleb.resume_app.model.response.EducationResponse;
import a.gleb.resume_app.model.response.ResumeResponse;
import a.gleb.resume_app.model.response.WorkExperienceResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
public class ResumeMapper {

    public ResumeResponse toResponse(ResumeEntity entity) {
        return toResponse(
                entity,
                entity.getSkills(),
                entity.getWorkExperiences(),
                entity.getEducations(),
                entity.getLinks()
        );
    }

    public ResumeResponse toResponse(
            ResumeEntity entity,
            List<SkillEntity> skills,
            List<WorkExperienceEntity> workExperiences,
            List<EducationEntity> educations,
            List<ResumeExternalLinkEntity> links) {
        return ResumeResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .desiredPosition(entity.getDesiredPosition())
                .summary(entity.getSummary())
                .birthDate(entity.getBirthDate())
                .photoUrl(entity.getPhotoUrl())
                .skills(skills.stream().map(SkillEntity::getName).toList())
                .experience(workExperiences.stream().map(this::toWorkExperienceResponse).toList())
                .education(educations.stream().map(this::toEducationResponse).toList())
                .links(links.stream().map(ResumeExternalLinkEntity::getUrl).toList())
                .createdAt(entity.getCreatedAt() != null
                        ? entity.getCreatedAt().toInstant(ZoneOffset.UTC)
                        : null)
                .updatedAt(entity.getUpdatedAt() != null
                        ? entity.getUpdatedAt().toInstant(ZoneOffset.UTC)
                        : null)
                .build();
    }

    public ResumeEntity toResumeEntity(UUID accountId, ResumeRequest request) {
        return ResumeEntity.builder()
                .accountId(accountId)
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .desiredPosition(request.desiredPosition())
                .summary(request.summary())
                .birthDate(request.birthDate())
                .build();
    }

    public ResumeEntity toResumeEntity(ResumeEntity existing, ResumeRequest request) {
        return ResumeEntity.builder()
                .id(existing.getId())
                .accountId(existing.getAccountId())
                .createdAt(existing.getCreatedAt())
                .createdBy(existing.getCreatedBy())
                .photoUrl(existing.getPhotoUrl())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .desiredPosition(request.desiredPosition())
                .summary(request.summary())
                .birthDate(request.birthDate())
                .build();
    }

    public List<SkillEntity> toSkillEntities(List<String> names, ResumeEntity resume) {
        if (names == null) return List.of();
        return names.stream()
                .map(name -> SkillEntity.builder().resume(resume).name(name).build())
                .toList();
    }

    public List<ResumeExternalLinkEntity> toLinkEntities(List<String> urls, ResumeEntity resume) {
        if (urls == null) return List.of();
        return urls.stream()
                .map(url -> ResumeExternalLinkEntity.builder().resume(resume).url(url).build())
                .toList();
    }

    public List<WorkExperienceEntity> toWorkExperienceEntities(List<WorkExperienceRequest> requests, ResumeEntity resume) {
        if (requests == null) return List.of();
        return requests.stream()
                .map(req -> (WorkExperienceEntity) WorkExperienceEntity.builder()
                        .resume(resume)
                        .company(req.company())
                        .position(req.position())
                        .startDate(req.startDate())
                        .endDate(req.endDate())
                        .description(req.description())
                        .build())
                .toList();
    }

    public List<EducationEntity> toEducationEntities(List<EducationRequest> requests, ResumeEntity resume) {
        if (requests == null) return List.of();
        return requests.stream()
                .map(req -> (EducationEntity) EducationEntity.builder()
                        .resume(resume)
                        .institution(req.institution())
                        .degree(req.degree())
                        .fieldOfStudy(req.fieldOfStudy())
                        .startDate(req.startDate())
                        .endDate(req.endDate())
                        .build())
                .toList();
    }

    private WorkExperienceResponse toWorkExperienceResponse(WorkExperienceEntity entity) {
        return WorkExperienceResponse.builder()
                .company(entity.getCompany())
                .position(entity.getPosition())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .description(entity.getDescription())
                .build();
    }

    private EducationResponse toEducationResponse(EducationEntity entity) {
        return EducationResponse.builder()
                .institution(entity.getInstitution())
                .degree(entity.getDegree())
                .fieldOfStudy(entity.getFieldOfStudy())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }
}
