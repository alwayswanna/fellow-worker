package a.gleb.vacancy_app.db.spec;

import a.gleb.vacancy_app.db.entity.VacancyEntity;
import a.gleb.vacancy_app.db.entity.VacancySkillEntity;
import a.gleb.vacancy_app.model.enums.EmploymentType;
import a.gleb.vacancy_app.model.enums.ExperienceLevel;
import a.gleb.vacancy_app.model.enums.VacancyStatus;
import a.gleb.vacancy_app.model.enums.WorkFormat;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.UUID;

public class VacancySpecification {

    private VacancySpecification() {}

    public static Specification<VacancyEntity> withTitle(String title) {
        return (root, query, cb) -> title == null ? null
                : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<VacancyEntity> withCompanyId(UUID companyId) {
        return (root, query, cb) -> companyId == null ? null
                : cb.equal(root.get("companyId"), companyId);
    }

    public static Specification<VacancyEntity> withCity(String city) {
        return (root, query, cb) -> city == null ? null
                : cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    public static Specification<VacancyEntity> withEmploymentType(EmploymentType employmentType) {
        return (root, query, cb) -> employmentType == null ? null
                : cb.equal(root.get("employmentType"), employmentType);
    }

    public static Specification<VacancyEntity> withWorkFormat(WorkFormat workFormat) {
        return (root, query, cb) -> workFormat == null ? null
                : cb.equal(root.get("workFormat"), workFormat);
    }

    public static Specification<VacancyEntity> withExperienceLevel(ExperienceLevel experienceLevel) {
        return (root, query, cb) -> experienceLevel == null ? null
                : cb.equal(root.get("experienceLevel"), experienceLevel);
    }

    public static Specification<VacancyEntity> withSalaryFrom(Long salaryFrom) {
        return (root, query, cb) -> salaryFrom == null ? null
                : cb.greaterThanOrEqualTo(root.get("salaryTo"), salaryFrom);
    }

    public static Specification<VacancyEntity> withSalaryTo(Long salaryTo) {
        return (root, query, cb) -> salaryTo == null ? null
                : cb.lessThanOrEqualTo(root.get("salaryFrom"), salaryTo);
    }

    public static Specification<VacancyEntity> withStatus(VacancyStatus status) {
        return (root, query, cb) -> status == null ? null
                : cb.equal(root.get("status"), status);
    }

    public static Specification<VacancyEntity> withSkills(List<String> skills) {
        if (skills == null || skills.isEmpty()) return (root, query, cb) -> null;
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            var skillRoot = subquery.from(VacancySkillEntity.class);
            subquery.select(cb.count(skillRoot))
                    .where(
                            cb.equal(skillRoot.get("vacancy"), root),
                            skillRoot.get("name").in(skills)
                    );
            return cb.greaterThan(subquery, 0L);
        };
    }

    public static Specification<VacancyEntity> build(
            String title,
            UUID companyId,
            String city,
            EmploymentType employmentType,
            WorkFormat workFormat,
            ExperienceLevel experienceLevel,
            Long salaryFrom,
            Long salaryTo,
            VacancyStatus status,
            List<String> skills
    ) {
        return Specification.where(withTitle(title))
                .and(withCompanyId(companyId))
                .and(withCity(city))
                .and(withEmploymentType(employmentType))
                .and(withWorkFormat(workFormat))
                .and(withExperienceLevel(experienceLevel))
                .and(withSalaryFrom(salaryFrom))
                .and(withSalaryTo(salaryTo))
                .and(withStatus(status))
                .and(withSkills(skills));
    }
}
