package a.gleb.company_app.domain.model;

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
public class CompanyReview {

    private UUID id;
    private UUID companyId;
    private UUID accountId;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}
