package a.gleb.resume_app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Resume {

    private UUID id;
    private UUID accountId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String desiredPosition;
    private String summary;
    private LocalDate birthDate;
    private String photoUrl;
    private List<String> skills;
    private List<WorkExperience> experience;
    private List<Education> education;
    private List<String> links;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private UUID createdBy;
    private UUID updatedBy;
}
