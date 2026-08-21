-- changeset a.gleb:005-create-outbox-events-table
CREATE TABLE company.outbox_events
(
    id              UUID        NOT NULL,
    aggregate_type  TEXT        NOT NULL,
    aggregate_id    UUID        NOT NULL,
    event_type      TEXT        NOT NULL,
    payload         TEXT        NOT NULL,
    status          TEXT        DEFAULT 'PENDING'          NOT NULL,
    retry_count     INT         DEFAULT 0                  NOT NULL,
    created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    processed_at    TIMESTAMP,
    error_message   TEXT,
    PRIMARY KEY (id)
);

CREATE INDEX idx_company_outbox_events_status_created_at ON company.outbox_events (status, created_at);

-- changeset a.gleb:006-create-shedlock-table
CREATE TABLE IF NOT EXISTS company.shedlock
(
    name        VARCHAR(64) PRIMARY KEY,
    lock_until  TIMESTAMP,
    locked_at   TIMESTAMP,
    locked_by   VARCHAR(255)
);
