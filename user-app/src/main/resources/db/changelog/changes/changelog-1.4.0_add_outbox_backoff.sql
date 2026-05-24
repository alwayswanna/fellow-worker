-- @formatter:off
ALTER TABLE outbox_events ADD COLUMN next_attempt_at timestamp DEFAULT CURRENT_TIMESTAMP;

CREATE INDEX idx_outbox_events_status_next_attempt_at ON outbox_events (status, next_attempt_at);
