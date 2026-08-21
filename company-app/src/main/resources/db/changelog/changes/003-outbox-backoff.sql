-- liquibase formatted sql

-- changeset a.gleb:007-add-outbox-next-attempt-at
ALTER TABLE company.outbox_events ADD COLUMN next_attempt_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_company_outbox_events_status_next_attempt_at ON company.outbox_events (status, next_attempt_at);
