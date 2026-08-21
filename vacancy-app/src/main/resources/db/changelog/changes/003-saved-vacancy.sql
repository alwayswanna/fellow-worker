-- liquibase formatted sql

-- changeset a.gleb:006-create-saved-vacancy-table
CREATE TABLE vacancy.saved_vacancy
(
    id          UUID      PRIMARY KEY,
    vacancy_id  UUID      NOT NULL REFERENCES vacancy.vacancy (id) ON DELETE CASCADE,
    account_id  UUID      NOT NULL,
    created_at  TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID,
    CONSTRAINT uq_saved_vacancy_vacancy_account UNIQUE (vacancy_id, account_id)
);

CREATE INDEX idx_saved_vacancy_vacancy_id ON vacancy.saved_vacancy (vacancy_id);
CREATE INDEX idx_saved_vacancy_account_id ON vacancy.saved_vacancy (account_id);
