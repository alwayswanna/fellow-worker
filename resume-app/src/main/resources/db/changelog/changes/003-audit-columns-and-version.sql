-- liquibase formatted sql

-- changeset a.gleb:003-resume-audit-columns-and-version
ALTER TABLE resume.resume ALTER COLUMN created_by TYPE UUID USING created_by::uuid;
ALTER TABLE resume.resume ALTER COLUMN updated_by TYPE UUID USING updated_by::uuid;
ALTER TABLE resume.resume ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- changeset a.gleb:004-work-experience-audit-columns-and-version
ALTER TABLE resume.work_experience ALTER COLUMN created_by TYPE UUID USING created_by::uuid;
ALTER TABLE resume.work_experience ALTER COLUMN updated_by TYPE UUID USING updated_by::uuid;
ALTER TABLE resume.work_experience ADD COLUMN version BIGINT NOT NULL DEFAULT 0;

-- changeset a.gleb:005-education-audit-columns-and-version
ALTER TABLE resume.education ALTER COLUMN created_by TYPE UUID USING created_by::uuid;
ALTER TABLE resume.education ALTER COLUMN updated_by TYPE UUID USING updated_by::uuid;
ALTER TABLE resume.education ADD COLUMN version BIGINT NOT NULL DEFAULT 0;
