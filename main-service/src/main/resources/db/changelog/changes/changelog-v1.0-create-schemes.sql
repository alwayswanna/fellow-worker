/*@formatter:off */
CREATE TYPE employment_type AS ENUM (
    'FULL_TIME',
    'PART_TIME',
    'CONTRACT',
    'TEMPORARY',
    'INTERNSHIP',
    'FREELANCE'
);

CREATE TYPE work_schedule AS ENUM (
    'FULL_DAY',
    'SHIFT_WORK',
    'FLEXIBLE',
    'REMOTE',
    'ROTATING'
);

CREATE TYPE experience_level AS ENUM (
    'INTERN',
    'JUNIOR',
    'MIDDLE',
    'SENIOR',
    'LEAD',
    'DIRECTOR'
);

CREATE TYPE currency_type AS ENUM (
    'USD', 'EUR', 'GBP', 'JPY', 'CNY', 'RUB', 'UAH', 'KZT',
    'CAD', 'AUD', 'CHF', 'PLN', 'TRY', 'INR', 'BRL'
);


CREATE TABLE IF NOT EXISTS resumes (
    id                          uuid                PRIMARY KEY DEFAULT gen_random_uuid(),
    title                       VARCHAR(255)        NOT NULL,
    description                 TEXT                NOT NULL,
    full_name                   VARCHAR(100)        NOT NULL,
    email                       VARCHAR(255)        NOT NULL,
    phone                       VARCHAR(25),
    salary_expectation          INTEGER,
    expected_salary_currency    TEXT,
    experience_years            INTEGER,
    employment_type             VARCHAR(25),
    work_schedule               VARCHAR(25),
    is_active                   BOOLEAN             NOT NULL DEFAULT TRUE,
    location                    VARCHAR(255),
    languages                   VARCHAR(255),
    created_at                  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS resume_skills (
    id                  UUID                PRIMARY KEY DEFAULT gen_random_uuid(),
    resume_id           UUID                NOT NULL,
    skill               VARCHAR(100)        NOT NULL,
    created_at          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_skills_resume
        FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS resume_educations (
    id                  UUID                PRIMARY KEY DEFAULT gen_random_uuid(),
    resume_id           UUID                NOT NULL,
    institution         VARCHAR(255)        NOT NULL,
    degree              VARCHAR(100)        NOT NULL,
    field_of_study      VARCHAR(100)        NOT NULL,
    start_date          DATE                NOT NULL,
    end_date            DATE,
    is_current          BOOLEAN             DEFAULT FALSE,
    description         TEXT,
    created_at          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_educations_resume
        FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS resume_working_experiences(
    id                  UUID                PRIMARY KEY DEFAULT gen_random_uuid(),
    resume_id           UUID                NOT NULL,
    company             VARCHAR(255)        NOT NULL,
    position            VARCHAR(100)        NOT NULL,
    start_date          DATE                NOT NULL,
    end_date            DATE,
    is_current          BOOLEAN             DEFAULT FALSE,
    description         TEXT,
    created_at          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_experiences_resume
        FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS resume_skills (
    id                  UUID                PRIMARY KEY DEFAULT gen_random_uuid(),
    resume_id           UUID,
    created_at          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_resume_skill_resume
        FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS vacancies (
    id                      UUID                PRIMARY KEY DEFAULT  gen_random_uuid(),
    title                   VARCHAR(255)        NOT NULL,
    description             TEXT NOT NULL,
    company_name            VARCHAR(100)        NOT NULL,
    contact_email           VARCHAR(255),
    salary_from             INTEGER,
    salary_to               INTEGER,
    salary_currency         currency_type,
    employment_type         employment_type,
    work_schedule           work_schedule,
    experience_level        experience_level,
    location                VARCHAR(255),
    is_remote               BOOLEAN             DEFAULT FALSE,
    is_active               BOOLEAN             NOT NULL DEFAULT TRUE,
    application_deadline    DATE,
    created_at              TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);


CREATE TABLE IF NOT EXISTS vacancy_requirements (
    vacancy_id      UUID                NOT NULL,
    requirement     VARCHAR(255)        NOT NULL,
    PRIMARY KEY (vacancy_id, requirement),
    CONSTRAINT fk_vacancy_requirements_vacancy
        FOREIGN KEY (vacancy_id) REFERENCES vacancies(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS vacancy_benefits (
    vacancy_id      UUID                        NOT NULL,
    benefit         VARCHAR(255)                NOT NULL,
    PRIMARY KEY (vacancy_id, benefit),
    CONSTRAINT fk_vacancy_benefits_vacancy
        FOREIGN KEY (vacancy_id) REFERENCES vacancies(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS outbox_messages(
     id                      UUID                PRIMARY KEY        DEFAULT gen_random_uuid(),
     message                 CHAR VARYING        NOT NULL,
     binding_name            TEXT                NOT NULL,
     is_sent                 BOOLEAN             DEFAULT FALSE,
     created_at              TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
     updated_at              TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_resumes_email_idx ON resumes(email);
CREATE INDEX IF NOT EXISTS idx_resumes_is_active_idx ON resumes(is_active);
CREATE INDEX IF NOT EXISTS idx_resumes_created_at_idx ON resumes(created_at);
CREATE INDEX IF NOT EXISTS idx_resumes_employment_type_idx ON resumes(employment_type);

CREATE INDEX IF NOT EXISTS idx_vacancies_company_idx ON vacancies(company_name);
CREATE INDEX IF NOT EXISTS idx_vacancies_is_active_idx ON vacancies(is_active);
CREATE INDEX IF NOT EXISTS idx_vacancies_created_at_idx ON vacancies(created_at);
CREATE INDEX IF NOT EXISTS idx_vacancies_employment_type_idx ON vacancies(employment_type);
CREATE INDEX IF NOT EXISTS idx_vacancies_experience_level_idx ON vacancies(experience_level);
CREATE INDEX IF NOT EXISTS idx_vacancies_is_remote_idx ON vacancies(is_remote);

CREATE INDEX IF NOT EXISTS idx_resume_skills_skill_idx ON resume_skills(skill);
CREATE INDEX IF NOT EXISTS idx_resume_educations_resume_idx ON resume_educations(resume_id);
CREATE INDEX IF NOT EXISTS idx_resume_experiences_resume_idx ON resume_working_experiences(resume_id);

CREATE INDEX IF NOT EXISTS idx_vacancy_requirements_req_idx ON vacancy_requirements(requirement);
CREATE INDEX IF NOT EXISTS idx_vacancy_benefits_benefit_idx ON vacancy_benefits(benefit);

CREATE INDEX IF NOT EXISTS outbox_messages_is_sent_idx ON outbox_messages(is_sent);