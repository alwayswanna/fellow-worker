package a.gleb.resume_app.application.port.out;

import a.gleb.resume_app.domain.model.PageQuery;
import a.gleb.resume_app.domain.model.PageResult;
import a.gleb.resume_app.domain.model.Resume;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepositoryPort {

    Resume create(Resume resume);

    Resume update(Resume resume);

    Resume updatePhotoUrl(UUID id, String photoUrl);

    Optional<Resume> findByIdAndAccountId(UUID id, UUID accountId);

    boolean existsByIdAndAccountId(UUID id, UUID accountId);

    void deleteByIdAndAccountId(UUID id, UUID accountId);

    void deleteAllByAccountId(UUID accountId);

    List<Resume> findAllByAccountId(UUID accountId);

    PageResult<Resume> findAll(PageQuery pageQuery);
}
