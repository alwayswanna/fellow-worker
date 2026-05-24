-- liquibase formatted sql

-- changeset a.gleb:add-selectable-to-roles
ALTER TABLE roles ADD COLUMN selectable BOOLEAN NOT NULL DEFAULT TRUE;
