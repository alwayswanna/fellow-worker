package a.gleb.user_app.application.port.out;

import a.gleb.user_app.domain.model.PageQuery;
import a.gleb.user_app.domain.model.PageResult;
import a.gleb.user_app.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    boolean existsByLogin(String login);

    Optional<User> findByLogin(String login);

    Optional<String> findRoleCodeByLogin(String login);

    User save(User user);

    Optional<User> findById(UUID id);

    void deleteById(UUID id);

    PageResult<User> findAll(PageQuery pageQuery);

    List<User> search(String query, PageQuery pageQuery);
}
