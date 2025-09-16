package a.gleb.user_service.db.scpecification;

import a.gleb.apicommon.api.user_service.role.Role.RoleFilterRequest;
import a.gleb.user_service.db.entity.RoleEntity;
import a.gleb.user_service.db.entity.RoleEntity_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

public final class RoleEntitySpecification {

    private RoleEntitySpecification() {
    }

    public static Specification<RoleEntity> buildRoleEntitySpecificationByFilter(RoleFilterRequest request) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (!CollectionUtils.isEmpty(request.ids())) {
                return root.get(RoleEntity_.ID).in(request.ids());
            }

            if (StringUtils.isNotEmpty(request.roleName())) {
                predicates.add(cb.like(root.get(RoleEntity_.ROLE_NAME), request.roleName()));
            }

            if (StringUtils.isNotEmpty(request.displayName())) {
                predicates.add(cb.like(root.get(RoleEntity_.DISPLAY_NAME), request.displayName()));
            }

            return predicates.stream().reduce(cb.and(), cb::and);
        };
    }
}
