package a.gleb.vacancy_app.adapter.out.persistence.repository;

import a.gleb.vacancy_app.adapter.out.persistence.entity.SavedVacancyEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SavedVacancyEntityRepository extends JpaRepository<SavedVacancyEntity, UUID> {

    Optional<SavedVacancyEntity> findByVacancyIdAndAccountId(UUID vacancyId, UUID accountId);

    boolean existsByVacancyIdAndAccountId(UUID vacancyId, UUID accountId);

    Page<SavedVacancyEntity> findAllByAccountId(UUID accountId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM SavedVacancyEntity s WHERE s.accountId = :accountId")
    void deleteAllByAccountId(@Param("accountId") UUID accountId);
}
