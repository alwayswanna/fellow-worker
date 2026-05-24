-- changeset a.gleb:002-create-company-table
CREATE TABLE company.company
(
    id                UUID            PRIMARY KEY,
    owner_account_id  UUID            NOT NULL UNIQUE,
    name              VARCHAR(200)    NOT NULL,
    description       TEXT,
    website           VARCHAR(500),
    logo_url          VARCHAR(1000),
    industry          VARCHAR(100),
    size              VARCHAR(20),
    city              VARCHAR(100),
    country           VARCHAR(100),
    rating            NUMERIC(4, 2)   NOT NULL DEFAULT 0,
    review_count      INT             NOT NULL DEFAULT 0,
    created_at        TIMESTAMP       NOT NULL,
    updated_at        TIMESTAMP,
    created_by        UUID,
    updated_by        UUID
);

CREATE INDEX idx_company_name         ON company.company (name);
CREATE INDEX idx_company_industry     ON company.company (industry);
CREATE INDEX idx_company_city         ON company.company (city);
CREATE INDEX idx_company_owner        ON company.company (owner_account_id);

-- changeset a.gleb:003-create-company-recruiter-table
CREATE TABLE company.company_recruiter
(
    id          UUID        PRIMARY KEY,
    company_id  UUID        NOT NULL REFERENCES company.company (id) ON DELETE CASCADE,
    account_id  UUID        NOT NULL UNIQUE,
    created_at  TIMESTAMP   NOT NULL,
    updated_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID
);

CREATE INDEX idx_company_recruiter_company_id ON company.company_recruiter (company_id);
CREATE INDEX idx_company_recruiter_account_id ON company.company_recruiter (account_id);

-- changeset a.gleb:004-create-company-review-table
CREATE TABLE company.company_review
(
    id          UUID        PRIMARY KEY,
    company_id  UUID        NOT NULL REFERENCES company.company (id) ON DELETE CASCADE,
    account_id  UUID        NOT NULL,
    rating      SMALLINT    NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     TEXT,
    created_at  TIMESTAMP   NOT NULL,
    updated_at  TIMESTAMP,
    created_by  UUID,
    updated_by  UUID,
    CONSTRAINT uq_company_review_account UNIQUE (company_id, account_id)
);

CREATE INDEX idx_company_review_company_id ON company.company_review (company_id);
