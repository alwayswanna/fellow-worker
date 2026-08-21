package a.gleb.vacancy_app.db.repository;

import a.gleb.vacancy_app.db.entity.ApplicationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<ApplicationEntity, UUID> {

    Optional<ApplicationEntity> findByVacancyIdAndApplicantAccountId(UUID vacancyId, UUID applicantAccountId);

    boolean existsByVacancyIdAndApplicantAccountId(UUID vacancyId, UUID applicantAccountId);

    Page<ApplicationEntity> findAllByVacancyId(UUID vacancyId, Pageable pageable);

    List<ApplicationEntity> findAllByApplicantAccountId(UUID applicantAccountId);

    @Modifying
    @Query("DELETE FROM ApplicationEntity a WHERE a.applicantAccountId = :accountId")
    void deleteAllByApplicantAccountId(@Param("accountId") UUID accountId);
}
