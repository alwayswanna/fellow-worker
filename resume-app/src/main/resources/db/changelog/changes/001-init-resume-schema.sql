-- liquibase formatted sql

-- changeset a.gleb:002-create-resume-table
CREATE TABLE resume.resume
(
    id               UUID               PRIMARY KEY,
    account_id       UUID               NOT NULL,
    first_name       VARCHAR(100)       NOT NULL,
    last_name        VARCHAR(100)       NOT NULL,
    email            VARCHAR(254)       NOT NULL,
    phone            VARCHAR(20),
    desired_position VARCHAR(100),
    summary          TEXT,
    created_at       TIMESTAMP          NOT NULL,
    updated_at       TIMESTAMP,
    created_by       VARCHAR(100),
    updated_by       VARCHAR(100)
);

CREATE INDEX idx_resume_account_id ON resume.resume (account_id);

-- changeset a.gleb:003-create-work-experience-table
CREATE TABLE resume.work_experience
(
    id                  UUID            PRIMARY KEY,
    resume_id           UUID            NOT NULL REFERENCES resume.resume (id) ON DELETE CASCADE,
    company             VARCHAR(150)    NOT NULL,
    position            VARCHAR(150)    NOT NULL,
    start_date          DATE,
    end_date            DATE,
    description         TEXT,
    created_at          TIMESTAMP       NOT NULL,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100)
);

CREATE INDEX idx_work_experience_resume_id ON resume.work_experience (resume_id);

-- changeset a.gleb:004-create-education-table
CREATE TABLE resume.education
(
    id                  UUID             PRIMARY KEY,
    resume_id           UUID             NOT NULL REFERENCES resume.resume (id) ON DELETE CASCADE,
    institution         VARCHAR(200)     NOT NULL,
    degree              VARCHAR(150)     NOT NULL,
    field_of_study      VARCHAR(150)     NOT NULL,
    start_date          DATE,
    end_date            DATE,
    created_at          TIMESTAMP        NOT NULL,
    updated_at          TIMESTAMP,
    created_by          VARCHAR(100),
    updated_by          VARCHAR(100)
);

CREATE INDEX idx_education_resume_id ON resume.education (resume_id);

-- changeset a.gleb:005-create-skill-table
CREATE TABLE resume.skill
(
    id        UUID PRIMARY KEY,
    resume_id UUID         NOT NULL REFERENCES resume.resume (id) ON DELETE CASCADE,
    name      VARCHAR(100) NOT NULL
);

CREATE INDEX idx_skill_resume_id ON resume.skill (resume_id);

-- changeset a.gleb:006-create-resume-link-table
CREATE TABLE resume.resume_link
(
    id        UUID PRIMARY KEY,
    resume_id UUID         NOT NULL REFERENCES resume.resume (id) ON DELETE CASCADE,
    url       VARCHAR(500) NOT NULL
);

CREATE INDEX idx_resume_link_resume_id ON resume.resume_link (resume_id);
