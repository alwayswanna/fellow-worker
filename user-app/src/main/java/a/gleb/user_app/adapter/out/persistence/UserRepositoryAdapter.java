package a.gleb.user_app.adapter.out.persistence;

import a.gleb.user_app.adapter.out.persistence.mapper.UserPersistenceMapper;
import a.gleb.user_app.adapter.out.persistence.projection.UserRoleCodeProjection;
import a.gleb.user_app.adapter.out.persistence.repository.UserEntityRepository;
import a.gleb.user_app.application.port.out.UserRepositoryPort;
import a.gleb.user_app.domain.model.PageQuery;
import a.gleb.user_app.domain.model.PageResult;
import a.gleb.user_app.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepositoryPort {

    private final UserEntityRepository userEntityRepository;
    private final UserPersistenceMapper userPersistenceMapper;

    @Override
    public boolean existsByLogin(String login) {
        return userEntityRepository.existsUserEntityByLogin(login);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return userEntityRepository.findUserEntityByLogin(login).map(userPersistenceMapper::toDomain);
    }

    @Override
    public Optional<String> findRoleCodeByLogin(String login) {
        return userEntityRepository.findRoleCodeByLogin(login).map(UserRoleCodeProjection::getRoleCode);
    }

    @Override
    public User save(User user) {
        var saved = userEntityRepository.save(userPersistenceMapper.toEntity(user));
        return userPersistenceMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return userEntityRepository.findById(id).map(userPersistenceMapper::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        userEntityRepository.deleteById(id);
    }

    @Override
    public PageResult<User> findAll(PageQuery pageQuery) {
        var page = userEntityRepository.findAll(PersistencePageSupport.toPageable(pageQuery));
        return PersistencePageSupport.toPageResult(page, userPersistenceMapper::toDomain);
    }

    @Override
    public List<User> search(String query, PageQuery pageQuery) {
        var page = userEntityRepository.searchByQuery(query, PersistencePageSupport.toPageable(pageQuery));
        return page.stream().map(userPersistenceMapper::toDomainShort).toList();
    }
}
