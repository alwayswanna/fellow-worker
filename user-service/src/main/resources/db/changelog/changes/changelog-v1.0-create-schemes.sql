/*@formatter:off */
CREATE TABLE oauth2_authorization
(
    id                            uuid         NOT NULL,
    registered_client_id          varchar(100) NOT NULL,
    principal_name                varchar(200) NOT NULL,
    authorization_grant_type      varchar(100) NOT NULL,
    authorized_scopes             varchar(1000) DEFAULT NULL,
    attributes                    text          DEFAULT NULL,
    state                         varchar(500)  DEFAULT NULL,
    authorization_code_value      text          DEFAULT NULL,
    authorization_code_issued_at  timestamp     DEFAULT NULL,
    authorization_code_expires_at timestamp     DEFAULT NULL,
    authorization_code_metadata   text          DEFAULT NULL,
    access_token_value            text          DEFAULT NULL,
    access_token_issued_at        timestamp     DEFAULT NULL,
    access_token_expires_at       timestamp     DEFAULT NULL,
    access_token_metadata         text          DEFAULT NULL,
    access_token_type             varchar(100)  DEFAULT NULL,
    access_token_scopes           varchar(1000) DEFAULT NULL,
    oidc_id_token_value           text          DEFAULT NULL,
    oidc_id_token_issued_at       timestamp     DEFAULT NULL,
    oidc_id_token_claims          jsonb         DEFAULT NULL,
    oidc_id_token_expires_at      timestamp     DEFAULT NULL,
    oidc_id_token_metadata        text          DEFAULT NULL,
    refresh_token_value           text          DEFAULT NULL,
    refresh_token_issued_at       timestamp     DEFAULT NULL,
    refresh_token_expires_at      timestamp     DEFAULT NULL,
    refresh_token_metadata        text          DEFAULT NULL,
    user_code_value               text          DEFAULT NULL,
    user_code_issued_at           timestamp     DEFAULT NULL,
    user_code_expires_at          timestamp     DEFAULT NULL,
    user_code_metadata            text          DEFAULT NULL,
    device_code_value             text          DEFAULT NULL,
    device_code_issued_at         timestamp     DEFAULT NULL,
    device_code_expires_at        timestamp     DEFAULT NULL,
    device_code_metadata          text          DEFAULT NULL,
    date_create                   timestamp     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE TABLE oauth2_registered_client
(
    id                            uuid                                    NOT NULL,
    client_id                     varchar(100)                            NOT NULL,
    client_id_issued_at           timestamp     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    client_secret                 varchar(200)  DEFAULT NULL,
    client_secret_expires_at      timestamp     DEFAULT NULL,
    client_name                   varchar(200)                            NOT NULL,
    client_authentication_methods varchar(1000)                           NOT NULL,
    authorization_grant_types     varchar(1000)                           NOT NULL,
    redirect_uris                 varchar(1000) DEFAULT NULL,
    post_logout_redirect_uris     varchar(1000) DEFAULT NULL,
    scopes                        varchar(1000)                           NOT NULL,
    client_settings               varchar(2000)                           NOT NULL,
    token_settings                varchar(2000)                           NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE role
(
    id              uuid                NOT NULL,
    role_name       varchar(30)         NOT NULL UNIQUE,
    display_name    varchar(50)         NOT NULL,
    updated_at      timestamp           DEFAULT current_timestamp,
    created_at      timestamp           DEFAULT current_timestamp,
    is_system_role  boolean             DEFAULT TRUE,
    version         bigint,
    PRIMARY KEY (id)
);

CREATE INDEX ON role (role_name);

CREATE TABLE account
(
    id              uuid            NOT NULL,
    username        varchar(30)     NOT NULL UNIQUE,
    password        varchar(250)    NOT NULL,
    first_name      varchar(30)     NOT NULL,
    middle_name     varchar(30)     NOT NULL,
    last_name       varchar(30),
    email           varchar(30)     NOT NULL,
    birth_date      date            NOT NULL,
    enabled         boolean         NOT NULL,
    role_id         uuid            NOT NULL,
    phone_number    text            NOT NULL,
    updated_at      timestamp       DEFAULT current_timestamp,
    created_at      timestamp       DEFAULT current_timestamp,
    version         bigint,
    FOREIGN KEY (role_id) REFERENCES role (id)
        ON DELETE SET NULL,
    PRIMARY KEY (id)
);

CREATE INDEX account_username_idx ON account (username);
CREATE INDEX account_phone_number_idx ON account (phone_number);
CREATE INDEX account_email_idx ON account (email);

CREATE TABLE registered_client_role (
    client_id       uuid        NOT NULL,
    role_id         uuid        NOT NULL,
    FOREIGN KEY (client_id) REFERENCES oauth2_registered_client (id),
    FOREIGN KEY (role_id) REFERENCES role (id)
);

CREATE TABLE outbox_message (
    id              uuid            PRIMARY KEY,
    message         char varying    NOT NULL,
    binding_name    text            NOT NULL,
    is_sent         boolean         DEFAULT FALSE,
    created         timestamp       DEFAULT current_timestamp,
    version         bigint
);

CREATE INDEX ON outbox_message(is_sent);

CREATE TABLE shedlock
(
    name       character varying(64)    PRIMARY KEY,
    lock_until timestamp                NOT NULL,
    locked_at  timestamp                NOT NULL,
    locked_by  character varying(255)   NOT NULL
);
