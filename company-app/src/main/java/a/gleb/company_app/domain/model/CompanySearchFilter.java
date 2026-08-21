package a.gleb.company_app.domain.model;

import a.gleb.company_app.domain.model.enums.CompanySize;

public record CompanySearchFilter(
        String name,
        String industry,
        String city,
        CompanySize size
) {
}
