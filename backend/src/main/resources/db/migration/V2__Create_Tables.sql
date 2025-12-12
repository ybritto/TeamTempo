CREATE SEQUENCE app_user_key_id_seq;
CREATE TABLE app_user
(
    KEY_ID     serial PRIMARY KEY,
    UUID UUID NOT NULL DEFAULT uuid_generate_v4(),
    NAME       varchar(200) NOT NULL,
    EMAIL      varchar(100) NOT NULL,
    PASSWORD   varchar(255) NOT NULL,
    ENABLED    boolean      NOT NULL DEFAULT true,
    ROLE       VARCHAR(59)  NOT NULL DEFAULT 'USER',

    CREATED_AT timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT app_user_uuid_unique UNIQUE (uuid),
    CONSTRAINT app_user_email_unique UNIQUE (email)
);

CREATE SEQUENCE team_key_id_seq;
CREATE TABLE team
(
    KEY_ID      serial PRIMARY KEY,
    UUID UUID NOT NULL DEFAULT uuid_generate_v4(),
    NAME        varchar(200)  NOT NULL,
    DESCRIPTION varchar(1000) NOT NULL,
    START_DATE  DATE          NOT NULL,
    END_DATE    DATE,
    APP_USER_ID int           NOT NULL,

    CREATED_AT  timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT  timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT team_uuid_unique UNIQUE (uuid),
    CONSTRAINT fk_team_app_user FOREIGN KEY (APP_USER_ID) REFERENCES app_user (KEY_ID) ON DELETE RESTRICT
);

CREATE SEQUENCE project_key_id_seq;
CREATE TABLE project
(
    KEY_ID      serial PRIMARY KEY,
    UUID UUID NOT NULL DEFAULT uuid_generate_v4(),
    NAME        varchar(200)  NOT NULL,
    DESCRIPTION varchar(1000) NOT NULL,
    START_DATE  DATE          NOT NULL,
    END_DATE    DATE,
    IS_ACTIVE   BOOLEAN       NOT NULL DEFAULT TRUE,
    TEAM_ID     int           NOT NULL,

    CREATED_AT  timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT  timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT project_uuid_unique UNIQUE (uuid),
    CONSTRAINT fk_project_team FOREIGN KEY (TEAM_ID) REFERENCES team (KEY_ID) ON DELETE RESTRICT
);

CREATE SEQUENCE project_configuration_key_id_seq;
CREATE TABLE project_configuration
(
    KEY_ID                  serial PRIMARY KEY,
    UUID UUID NOT NULL DEFAULT uuid_generate_v4(),
    ITERATION_DURATION      int         NOT NULL,
    ITERATION_DURATION_UNIT varchar(20) NOT NULL,
    CAPACITY_UNIT           varchar(20) NOT NULL,
    FORCAST_UNIT            varchar(20) NOT NULL,
    IS_ACTIVE               boolean     NOT NULL DEFAULT false,
    PROJECT_ID              int         NOT NULL,

    CREATED_AT              timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT              timestamp   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT project_configuration_uuid_unique UNIQUE (uuid),
    CONSTRAINT fk_project_team FOREIGN KEY (PROJECT_ID) REFERENCES project (KEY_ID) ON DELETE RESTRICT

);

CREATE SEQUENCE iteration_key_id_seq;

CREATE TABLE iteration
(
    KEY_ID              serial PRIMARY KEY,
    UUID UUID NOT NULL DEFAULT uuid_generate_v4(),
    NAME                varchar(200) NOT NULL,
    PROJECT_KEY_ID      int          NOT NULL,

    PLANNED_START_DATE  DATE,
    PLANNED_END_DATE    DATE,
    PLANNED_CAPACITY    INTEGER,
    PLANNED_FORECAST    INTEGER,

    ACTUAL_START_DATE   DATE,
    ACTUAL_END_DATE     DATE,
    ACTUAL_CAPACITY     INTEGER,
    ACTUAL_FORECAST     INTEGER,

    CREATED_AT          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT          timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT iteration_uuid_unique UNIQUE (uuid),
    CONSTRAINT fk_iteration_project FOREIGN KEY (PROJECT_KEY_ID) REFERENCES project (KEY_ID) ON DELETE RESTRICT
);