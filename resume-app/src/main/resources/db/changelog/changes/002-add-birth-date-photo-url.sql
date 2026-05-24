-- liquibase formatted sql

-- changeset a.gleb:002-add-birth-date-photo-url
ALTER TABLE resume.resume ADD COLUMN birth_date DATE;
ALTER TABLE resume.resume ADD COLUMN photo_url  VARCHAR(1000);
