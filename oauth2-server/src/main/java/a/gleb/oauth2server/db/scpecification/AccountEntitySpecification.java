package a.gleb.oauth2server.db.scpecification;

import a.gleb.oauth2server.db.entity.AccountEntity;
import a.gleb.oauth2server.model.Account.AccountFilterRequest;
import org.springframework.data.jpa.domain.Specification;

public final class AccountEntitySpecification {

    private AccountEntitySpecification() {
    }

    /**
     * Build specification for search account entities.
     */
    public static Specification<AccountEntity> buildAccountEntitySpecificationByFilter(AccountFilterRequest request) {
        return null;
    }
}
