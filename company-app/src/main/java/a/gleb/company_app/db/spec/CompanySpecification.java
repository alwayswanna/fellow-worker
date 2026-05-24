package a.gleb.company_app.db.spec;

import a.gleb.company_app.db.entity.CompanyEntity;
import a.gleb.company_app.model.enums.CompanySize;
import org.springframework.data.jpa.domain.Specification;

public class CompanySpecification {

    private CompanySpecification() {}

    public static Specification<CompanyEntity> withName(String name) {
        return (root, query, cb) -> name == null ? null
                : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<CompanyEntity> withIndustry(String industry) {
        return (root, query, cb) -> industry == null ? null
                : cb.equal(cb.lower(root.get("industry")), industry.toLowerCase());
    }

    public static Specification<CompanyEntity> withCity(String city) {
        return (root, query, cb) -> city == null ? null
                : cb.equal(cb.lower(root.get("city")), city.toLowerCase());
    }

    public static Specification<CompanyEntity> withSize(CompanySize size) {
        return (root, query, cb) -> size == null ? null
                : cb.equal(root.get("size"), size);
    }

    public static Specification<CompanyEntity> build(String name, String industry, String city, CompanySize size) {
        return Specification.where(withName(name))
                .and(withIndustry(industry))
                .and(withCity(city))
                .and(withSize(size));
    }
}
