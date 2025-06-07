package a.gleb.oauth2server.db.scpecification;

import a.gleb.oauth2server.db.entity.RoleEntity;
import a.gleb.oauth2server.model.Role;
import a.gleb.oauth2server.model.Role.RoleFilterRequest;
import org.springframework.data.jpa.domain.Specification;

public final class RoleEntitySpecification {

    private RoleEntitySpecification() {}

    public static Specification<RoleEntity> buildRoleEntitySpecificationByFilter(RoleFilterRequest request) {
        return null;
    }
}
