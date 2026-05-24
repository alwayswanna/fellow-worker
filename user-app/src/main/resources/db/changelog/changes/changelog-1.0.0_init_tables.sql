-- @formatter:off
CREATE TABLE client
(
    id                                  uuid,
    client_id                           text                                      NOT NULL,
    client_id_issued_at                 timestamp     DEFAULT CURRENT_TIMESTAMP   NOT NULL,
    client_secret                       text          DEFAULT NULL,
    client_secret_expires_at            timestamp     DEFAULT NULL,
    client_name                         text                                      NOT NULL,
    client_authentication_methods       text                                      NOT NULL,
    authorization_grant_types           text                                      NOT NULL,
    redirect_uris                       text          DEFAULT NULL,
    post_logout_redirect_uris           text          DEFAULT NULL,
    scopes                              text                                      NOT NULL,
    client_settings                     text                                      NOT NULL,
    token_settings                      text                                      NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX idx_client_client_id ON client (client_id);
CREATE INDEX idx_client_client_name ON client (client_name);

CREATE TABLE "authorization"
(
    id                                  uuid                  NOT NULL,
    registered_client_id                text                  NOT NULL,
    principal_name                      text                  NOT NULL,
    authorization_grant_type            text                  NOT NULL,
    authorized_scopes                   text                  DEFAULT NULL,
    attributes                          text                  DEFAULT NULL,
    state                               text                  DEFAULT NULL,
    authorization_code_value            text                  DEFAULT NULL,
    authorization_code_issued_at        timestamp             DEFAULT NULL,
    authorization_code_expires_at       timestamp             DEFAULT NULL,
    authorization_code_metadata         text                  DEFAULT NULL,
    access_token_value                  text                  DEFAULT NULL,
    access_token_issued_at              timestamp             DEFAULT NULL,
    access_token_expires_at             timestamp             DEFAULT NULL,
    access_token_metadata               text                  DEFAULT NULL,
    access_token_type                   text                  DEFAULT NULL,
    access_token_scopes                 text                  DEFAULT NULL,
    refresh_token_value                 text                  DEFAULT NULL,
    refresh_token_issued_at             timestamp             DEFAULT NULL,
    refresh_token_expires_at            timestamp             DEFAULT NULL,
    refresh_token_metadata              text                  DEFAULT NULL,
    oidc_id_token_value                 text                  DEFAULT NULL,
    oidc_id_token_issued_at             timestamp             DEFAULT NULL,
    oidc_id_token_expires_at            timestamp             DEFAULT NULL,
    oidc_id_token_metadata              text                  DEFAULT NULL,
    oidc_id_token_claims                text                  DEFAULT NULL,
    user_code_value                     text                  DEFAULT NULL,
    user_code_issued_at                 timestamp             DEFAULT NULL,
    user_code_expires_at                timestamp             DEFAULT NULL,
    user_code_metadata                  text                  DEFAULT NULL,
    device_code_value                   text                  DEFAULT NULL,
    device_code_issued_at               timestamp             DEFAULT NULL,
    device_code_expires_at              timestamp             DEFAULT NULL,
    device_code_metadata                text                  DEFAULT NULL,
    created_at                          timestamp             DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id, created_at)
) PARTITION BY RANGE (created_at);

CREATE TABLE roles (
    id              uuid,
    display_name    text                                                NOT NULL UNIQUE,
    code            text                                                NOT NULL,
    created_at      timestamp       DEFAULT CURRENT_TIMESTAMP           NOT NULL,
    updated_at      timestamp,
    created_by      text,
    updated_by      text,
    PRIMARY KEY (id)
);

CREATE TABLE users
(
    id              uuid,
    login           text                                                NOT NULL UNIQUE,
    first_name      text                                                NOT NULL,
    last_name       text                                                NOT NULL,
    birth_date      date                                                NOT NULL,
    password        text                                                NOT NULL,
    role_id         uuid                                                NOT NULL,
    created_at      timestamp       DEFAULT CURRENT_TIMESTAMP           NOT NULL,
    updated_at      timestamp,
    created_by      text,
    updated_by      text,
    CONSTRAINT fk_users_role_id
        FOREIGN KEY (role_id)
            REFERENCES roles(id),
    PRIMARY KEY (id)
);

CREATE INDEX idx_users_login ON users (login);

CREATE TABLE IF NOT EXISTS shedlock
(
    name                VARCHAR(64)         PRIMARY KEY,
    lock_until          TIMESTAMP,
    locked_at           TIMESTAMP,
    locked_by           VARCHAR(255)
);
