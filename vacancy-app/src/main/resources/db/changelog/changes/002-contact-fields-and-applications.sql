-- liquibase formatted sql

-- changeset a.gleb:004-add-contact-fields-to-vacancy
ALTER TABLE vacancy.vacancy
    ADD COLUMN contact_name  VARCHAR(200),
    ADD COLUMN contact_email VARCHAR(200),
    ADD COLUMN contact_phone VARCHAR(50);

-- changeset a.gleb:005-create-application-table
CREATE TABLE vacancy.application
(
    id                   UUID         PRIMARY KEY,
    vacancy_id           UUID         NOT NULL REFERENCES vacancy.vacancy (id) ON DELETE CASCADE,
    applicant_account_id UUID         NOT NULL,
    status               VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at           TIMESTAMP    NOT NULL,
    updated_at           TIMESTAMP,
    created_by           UUID,
    updated_by           UUID,
    CONSTRAINT uq_application_vacancy_applicant UNIQUE (vacancy_id, applicant_account_id)
);

CREATE INDEX idx_application_vacancy_id           ON vacancy.application (vacancy_id);
CREATE INDEX idx_application_applicant_account_id ON vacancy.application (applicant_account_id);
CREATE INDEX idx_application_status               ON vacancy.application (status);
