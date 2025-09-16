package a.gleb.user_service.db.scpecification;

import a.gleb.apicommon.api.user_service.account.Account.AccountFilterRequest;
import a.gleb.user_service.db.entity.AccountEntity;
import a.gleb.user_service.db.entity.AccountEntity_;
import a.gleb.user_service.db.entity.RoleEntity_;
import jakarta.persistence.criteria.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

public final class AccountEntitySpecification {

    private AccountEntitySpecification() {
    }

    /**
     * Build specification for search account entities.
     */
    public static Specification<AccountEntity> buildAccountEntitySpecificationByFilter(AccountFilterRequest request) {
        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (!CollectionUtils.isEmpty(request.ids())) {
                return root.get(AccountEntity_.ID).in(request.ids());
            }

            if (StringUtils.isNotEmpty(request.username())) {
                predicates.add(cb.like(root.get(AccountEntity_.USERNAME), request.username()));
            }

            if (StringUtils.isNotEmpty(request.email())) {
                predicates.add(cb.like(root.get(AccountEntity_.EMAIL), request.email()));
            }

            if (request.enabled() != null) {
                predicates.add(cb.equal(root.get(AccountEntity_.ENABLED), request.enabled()));
            }

            if (StringUtils.isNotEmpty(request.firstName())) {
                predicates.add(cb.like(root.get(AccountEntity_.FIRST_NAME), request.firstName()));
            }

            if (StringUtils.isNotEmpty(request.lastName())) {
                predicates.add(cb.like(root.get(AccountEntity_.LAST_NAME), request.lastName()));
            }

            if (StringUtils.isNotEmpty(request.middleName())) {
                predicates.add(cb.like(root.get(AccountEntity_.MIDDLE_NAME), request.middleName()));
            }

            if (!CollectionUtils.isEmpty(request.authorities())) {
                var roleJoin = root.join(AccountEntity_.role);
                predicates.add(roleJoin.get(RoleEntity_.ROLE_NAME).in(request.authorities()));
            }

            return predicates.stream().reduce(cb.and(), cb::and);
        };
    }
}
