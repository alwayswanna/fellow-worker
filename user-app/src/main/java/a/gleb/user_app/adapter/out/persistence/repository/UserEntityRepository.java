package a.gleb.user_app.adapter.out.persistence.repository;

import a.gleb.user_app.adapter.out.persistence.entity.UserEntity;
import a.gleb.user_app.adapter.out.persistence.projection.UserRoleCodeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {

    boolean existsUserEntityByLogin(String login);

    @EntityGraph(attributePaths = "role")
    Optional<UserEntity> findUserEntityByLogin(String login);

    @Query("SELECT r.code AS roleCode FROM UserEntity u JOIN u.role r WHERE u.login = :login")
    Optional<UserRoleCodeProjection> findRoleCodeByLogin(@Param("login") String login);

    /**
     * Not annotated with @EntityGraph: results are mapped via {@code UserPersistenceMapper.toShortDomain}-style
     * mapping which never accesses `role`, so eagerly joining it here would be a wasted fetch.
     */
    @Query("""
            SELECT u FROM UserEntity u
            WHERE LOWER(u.login)     LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(u.lastName)  LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<UserEntity> searchByQuery(@Param("query") String query, Pageable pageable);

    @Override
    @EntityGraph(attributePaths = "role")
    Optional<UserEntity> findById(UUID id);

    @Override
    @EntityGraph(attributePaths = "role")
    Page<UserEntity> findAll(Pageable pageable);
}
