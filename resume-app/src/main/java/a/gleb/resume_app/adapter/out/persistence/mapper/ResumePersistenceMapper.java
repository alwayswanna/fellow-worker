package a.gleb.resume_app.adapter.out.persistence.mapper;

import a.gleb.resume_app.adapter.out.persistence.entity.EducationEntity;
import a.gleb.resume_app.adapter.out.persistence.entity.ResumeEntity;
import a.gleb.resume_app.adapter.out.persistence.entity.ResumeExternalLinkEntity;
import a.gleb.resume_app.adapter.out.persistence.entity.SkillEntity;
import a.gleb.resume_app.adapter.out.persistence.entity.WorkExperienceEntity;
import a.gleb.resume_app.domain.model.Education;
import a.gleb.resume_app.domain.model.Resume;
import a.gleb.resume_app.domain.model.WorkExperience;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ResumePersistenceMapper {

    public ResumeEntity toEntity(Resume resume) {
        if (resume == null) {
            return null;
        }
        return ResumeEntity.builder()
                .id(resume.getId())
                .accountId(resume.getAccountId())
                .firstName(resume.getFirstName())
                .lastName(resume.getLastName())
                .email(resume.getEmail())
                .phone(resume.getPhone())
                .desiredPosition(resume.getDesiredPosition())
                .summary(resume.getSummary())
                .birthDate(resume.getBirthDate())
                .photoUrl(resume.getPhotoUrl())
                .createdAt(resume.getCreatedAt())
                .updatedAt(resume.getUpdatedAt())
                .createdBy(resume.getCreatedBy())
                .updatedBy(resume.getUpdatedBy())
                .build();
    }

    public List<SkillEntity> toSkillEntities(List<String> names, ResumeEntity resume) {
        if (names == null) {
            return List.of();
        }
        return names.stream()
                .map(name -> SkillEntity.builder().resume(resume).name(name).build())
                .toList();
    }

    public List<ResumeExternalLinkEntity> toLinkEntities(List<String> urls, ResumeEntity resume) {
        if (urls == null) {
            return List.of();
        }
        return urls.stream()
                .map(url -> ResumeExternalLinkEntity.builder().resume(resume).url(url).build())
                .toList();
    }

    public List<WorkExperienceEntity> toWorkExperienceEntities(List<WorkExperience> experience, ResumeEntity resume) {
        if (experience == null) {
            return List.of();
        }
        return experience.stream()
                .map(we -> (WorkExperienceEntity) WorkExperienceEntity.builder()
                        .resume(resume)
                        .company(we.company())
                        .position(we.position())
                        .startDate(we.startDate())
                        .endDate(we.endDate())
                        .description(we.description())
                        .build())
                .toList();
    }

    public List<EducationEntity> toEducationEntities(List<Education> education, ResumeEntity resume) {
        if (education == null) {
            return List.of();
        }
        return education.stream()
                .map(ed -> (EducationEntity) EducationEntity.builder()
                        .resume(resume)
                        .institution(ed.institution())
                        .degree(ed.degree())
                        .fieldOfStudy(ed.fieldOfStudy())
                        .startDate(ed.startDate())
                        .endDate(ed.endDate())
                        .build())
                .toList();
    }

    public Resume toDomain(
            ResumeEntity entity,
            List<SkillEntity> skills,
            List<WorkExperienceEntity> workExperiences,
            List<EducationEntity> educations,
            List<ResumeExternalLinkEntity> links
    ) {
        if (entity == null) {
            return null;
        }
        return Resume.builder()
                .id(entity.getId())
                .accountId(entity.getAccountId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .desiredPosition(entity.getDesiredPosition())
                .summary(entity.getSummary())
                .birthDate(entity.getBirthDate())
                .photoUrl(entity.getPhotoUrl())
                .skills(skills.stream().map(SkillEntity::getName).toList())
                .experience(workExperiences.stream().map(this::toWorkExperience).toList())
                .education(educations.stream().map(this::toEducation).toList())
                .links(links.stream().map(ResumeExternalLinkEntity::getUrl).toList())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private WorkExperience toWorkExperience(WorkExperienceEntity entity) {
        return WorkExperience.builder()
                .company(entity.getCompany())
                .position(entity.getPosition())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .description(entity.getDescription())
                .build();
    }

    private Education toEducation(EducationEntity entity) {
        return Education.builder()
                .institution(entity.getInstitution())
                .degree(entity.getDegree())
                .fieldOfStudy(entity.getFieldOfStudy())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .build();
    }
}
