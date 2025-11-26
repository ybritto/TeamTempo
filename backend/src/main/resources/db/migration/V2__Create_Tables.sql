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

CREATE TABLE team
(
    KEY_ID      serial PRIMARY KEY,
    UUID UUID NOT NULL DEFAULT uuid_generate_v4(),
    NAME        varchar(200)  NOT NULL,
    DESCRIPTION varchar(1000) NOT NULL,
    START_DATE  DATE          NOT NULL,
    END_DATE    DATE,
    APP_USER_ID int           NOT NULL,

    CREATED_AT timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT team_uuid_unique UNIQUE (uuid),
    CONSTRAINT fk_team_app_user FOREIGN KEY (APP_USER_ID) REFERENCES app_user (KEY_ID) ON DELETE RESTRICT
);

CREATE TABLE project
(
    KEY_ID      serial PRIMARY KEY,
    UUID UUID NOT NULL DEFAULT uuid_generate_v4(),
    NAME        varchar(200)  NOT NULL,
    DESCRIPTION varchar(1000) NOT NULL,
    START_DATE  DATE          NOT NULL,
    END_DATE    DATE,
    TEAM_ID int           NOT NULL,

    CREATED_AT timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT timestamp    NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT project_uuid_unique UNIQUE (uuid),
    CONSTRAINT fk_project_team FOREIGN KEY (TEAM_ID) REFERENCES team (KEY_ID) ON DELETE RESTRICT
);