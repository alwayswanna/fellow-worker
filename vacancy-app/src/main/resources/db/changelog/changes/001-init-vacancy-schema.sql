-- liquibase formatted sql
-- changeset a.gleb:002-create-vacancy-table
CREATE TABLE vacancy.vacancy
(
    id               UUID            PRIMARY KEY,
    company_id       UUID            NOT NULL,
    title            VARCHAR(200)    NOT NULL,
    description      TEXT,
    requirements     TEXT,
    salary_from      BIGINT,
    salary_to        BIGINT,
    currency         VARCHAR(10),
    employment_type  VARCHAR(30),
    work_format      VARCHAR(20),
    experience_level VARCHAR(20),
    city             VARCHAR(100),
    country          VARCHAR(100),
    status           VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',
    created_at       TIMESTAMP       NOT NULL,
    updated_at       TIMESTAMP,
    created_by       UUID,
    updated_by       UUID
);

CREATE INDEX idx_vacancy_company_id      ON vacancy.vacancy (company_id);
CREATE INDEX idx_vacancy_status          ON vacancy.vacancy (status);
CREATE INDEX idx_vacancy_employment_type ON vacancy.vacancy (employment_type);
CREATE INDEX idx_vacancy_work_format     ON vacancy.vacancy (work_format);
CREATE INDEX idx_vacancy_city            ON vacancy.vacancy (city);
CREATE INDEX idx_vacancy_salary          ON vacancy.vacancy (salary_from, salary_to);

-- changeset a.gleb:003-create-vacancy-skill-table
CREATE TABLE vacancy.vacancy_skill
(
    id         UUID            PRIMARY KEY,
    vacancy_id UUID            NOT NULL REFERENCES vacancy.vacancy (id) ON DELETE CASCADE,
    name       VARCHAR(100)    NOT NULL
);

CREATE INDEX idx_vacancy_skill_vacancy_id ON vacancy.vacancy_skill (vacancy_id);
CREATE INDEX idx_vacancy_skill_name       ON vacancy.vacancy_skill (name);
