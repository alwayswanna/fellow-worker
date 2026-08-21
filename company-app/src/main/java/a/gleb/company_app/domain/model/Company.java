package a.gleb.company_app.domain.model;

import a.gleb.company_app.domain.model.enums.CompanySize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Company {

    private UUID id;
    private UUID ownerAccountId;
    private String name;
    private String description;
    private String website;
    private String logoUrl;
    private String industry;
    private CompanySize size;
    private String city;
    private String country;
    private double rating;
    private int reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}
