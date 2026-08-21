package a.gleb.resume_app.adapter.in.web.mapper;

import a.gleb.resume_app.adapter.in.web.dto.EducationRequest;
import a.gleb.resume_app.adapter.in.web.dto.EducationResponse;
import a.gleb.resume_app.adapter.in.web.dto.ResumeRequest;
import a.gleb.resume_app.adapter.in.web.dto.ResumeResponse;
import a.gleb.resume_app.adapter.in.web.dto.WorkExperienceRequest;
import a.gleb.resume_app.adapter.in.web.dto.WorkExperienceResponse;
import a.gleb.resume_app.domain.model.Education;
import a.gleb.resume_app.domain.model.Resume;
import a.gleb.resume_app.domain.model.WorkExperience;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.List;

@Component
public class ResumeWebMapper {

    public Resume toDomain(ResumeRequest request) {
        return Resume.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .phone(request.phone())
                .desiredPosition(request.desiredPosition())
                .summary(request.summary())
                .birthDate(request.birthDate())
                .skills(request.skills())
                .experience(toExperienceDomain(request.experience()))
                .education(toEducationDomain(request.education()))
                .links(request.links())
                .build();
    }

    private List<WorkExperience> toExperienceDomain(List<WorkExperienceRequest> requests) {
        if (requests == null) {
            return null;
        }
        return requests.stream()
                .map(req -> WorkExperience.builder()
                        .company(req.company())
                        .position(req.position())
                        .startDate(req.startDate())
                        .endDate(req.endDate())
                        .description(req.description())
                        .build())
                .toList();
    }

    private List<Education> toEducationDomain(List<EducationRequest> requests) {
        if (requests == null) {
            return null;
        }
        return requests.stream()
                .map(req -> Education.builder()
                        .institution(req.institution())
                        .degree(req.degree())
                        .fieldOfStudy(req.fieldOfStudy())
                        .startDate(req.startDate())
                        .endDate(req.endDate())
                        .build())
                .toList();
    }

    public ResumeResponse toResponse(Resume resume) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .firstName(resume.getFirstName())
                .lastName(resume.getLastName())
                .email(resume.getEmail())
                .phone(resume.getPhone())
                .desiredPosition(resume.getDesiredPosition())
                .summary(resume.getSummary())
                .birthDate(resume.getBirthDate())
                .photoUrl(resume.getPhotoUrl())
                .skills(resume.getSkills())
                .experience(resume.getExperience().stream().map(this::toResponse).toList())
                .education(resume.getEducation().stream().map(this::toResponse).toList())
                .links(resume.getLinks())
                .createdAt(resume.getCreatedAt() != null ? resume.getCreatedAt().toInstant(ZoneOffset.UTC) : null)
                .updatedAt(resume.getUpdatedAt() != null ? resume.getUpdatedAt().toInstant(ZoneOffset.UTC) : null)
                .build();
    }

    private WorkExperienceResponse toResponse(WorkExperience workExperience) {
        return WorkExperienceResponse.builder()
                .company(workExperience.company())
                .position(workExperience.position())
                .startDate(workExperience.startDate())
                .endDate(workExperience.endDate())
                .description(workExperience.description())
                .build();
    }

    private EducationResponse toResponse(Education education) {
        return EducationResponse.builder()
                .institution(education.institution())
                .degree(education.degree())
                .fieldOfStudy(education.fieldOfStudy())
                .startDate(education.startDate())
                .endDate(education.endDate())
                .build();
    }
}
