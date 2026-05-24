-- @formatter:off
CREATE TABLE outbox_events
(
    id              uuid                                        NOT NULL,
    aggregate_type  text                                        NOT NULL,
    aggregate_id    uuid                                        NOT NULL,
    event_type      text                                        NOT NULL,
    payload         text                                        NOT NULL,
    status          text        DEFAULT 'PENDING'               NOT NULL,
    retry_count     int         DEFAULT 0                       NOT NULL,
    created_at      timestamp   DEFAULT CURRENT_TIMESTAMP       NOT NULL,
    processed_at    timestamp,
    error_message   text,
    PRIMARY KEY (id)
);

CREATE INDEX idx_outbox_events_status_created_at ON outbox_events (status, created_at);
