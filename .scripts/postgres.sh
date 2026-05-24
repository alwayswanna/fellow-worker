#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL

    ---------------------------------------------------------------------------
    -- Schemas
    ---------------------------------------------------------------------------
    CREATE SCHEMA IF NOT EXISTS resume;
    CREATE SCHEMA IF NOT EXISTS vacancy;
    CREATE SCHEMA IF NOT EXISTS "user";
    CREATE SCHEMA IF NOT EXISTS company;

    ---------------------------------------------------------------------------
    -- Users
    ---------------------------------------------------------------------------
    DO \$\$
    BEGIN
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'resume_user') THEN
            CREATE USER resume_user WITH PASSWORD 'resume_pass';
        END IF;
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'vacancy_user') THEN
            CREATE USER vacancy_user WITH PASSWORD 'vacancy_pass';
        END IF;
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'user_user') THEN
            CREATE USER user_user WITH PASSWORD 'user_pass';
        END IF;
        IF NOT EXISTS (SELECT FROM pg_catalog.pg_roles WHERE rolname = 'company_user') THEN
                    CREATE USER company_user WITH PASSWORD 'company_pass';
        END IF;
    END
    \$\$;

    ---------------------------------------------------------------------------
    -- Privileges: resume
    ---------------------------------------------------------------------------
    GRANT USAGE, CREATE ON SCHEMA resume TO resume_user;
    GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA resume TO resume_user;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA resume TO resume_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA resume
        GRANT ALL ON TABLES    TO resume_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA resume
        GRANT ALL ON SEQUENCES TO resume_user;
    ALTER ROLE resume_user SET search_path TO resume;

    ---------------------------------------------------------------------------
    -- Privileges: vacancy
    ---------------------------------------------------------------------------
    GRANT USAGE, CREATE ON SCHEMA vacancy TO vacancy_user;
    GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA vacancy TO vacancy_user;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA vacancy TO vacancy_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA vacancy
        GRANT ALL ON TABLES    TO vacancy_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA vacancy
        GRANT ALL ON SEQUENCES TO vacancy_user;
    ALTER ROLE vacancy_user SET search_path TO vacancy;

    ---------------------------------------------------------------------------
    -- Privileges: user
    ---------------------------------------------------------------------------
    GRANT USAGE, CREATE ON SCHEMA "user" TO user_user;
    GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA "user" TO user_user;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA "user" TO user_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA "user"
        GRANT ALL ON TABLES    TO user_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA "user"
        GRANT ALL ON SEQUENCES TO user_user;
    ALTER ROLE user_user SET search_path TO "user";

    ---------------------------------------------------------------------------
    -- Privileges: company
    ---------------------------------------------------------------------------
    GRANT USAGE, CREATE ON SCHEMA company TO company_user;
    GRANT ALL PRIVILEGES ON ALL TABLES    IN SCHEMA company TO company_user;
    GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA company TO company_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA company
        GRANT ALL ON TABLES    TO company_user;
    ALTER DEFAULT PRIVILEGES IN SCHEMA company
        GRANT ALL ON SEQUENCES TO company_user;
    ALTER ROLE company_user SET search_path TO company;

EOSQL