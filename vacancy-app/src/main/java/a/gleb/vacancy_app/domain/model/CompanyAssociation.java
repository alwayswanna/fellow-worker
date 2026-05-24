package a.gleb.vacancy_app.domain.model;

/**
 * Result of checking whether an account is the owner or a recruiter of a company — used to
 * authorize vacancy/application management, which is restricted to people associated with the
 * posting company (or platform admins, checked separately).
 */
public enum CompanyAssociation {
    ASSOCIATED,
    NOT_ASSOCIATED,
    COMPANY_NOT_FOUND
}
