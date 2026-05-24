-- @formatter:off
ALTER TABLE users RENAME COLUMN photo_url TO photo_key;

-- Previously stored values were full URLs (endpoint/bucket/objectKey); the app now only
-- persists the MinIO object key and resolves endpoint/bucket from configuration at read time.
UPDATE users SET photo_key = substring(photo_key from 'photos/[^/]+$') WHERE photo_key IS NOT NULL;
