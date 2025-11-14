CREATE TABLE app_user
(
    KEY_ID     serial PRIMARY KEY,
    UUID UUID NOT NULL DEFAULT uuid_generate_v4(),
    FULL_NAME  varchar(200)  NOT NULL,
    EMAIL      varchar(100) NOT NULL,
    PASSWORD   varchar(255)  NOT NULL,
    ENABLED    boolean       NOT NULL DEFAULT true,
    ROLE       VARCHAR(59)   NOT NULL DEFAULT 'USER',

    CREATED_AT timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UPDATED_AT timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT app_user_uuid_unique UNIQUE (uuid),
    CONSTRAINT app_user_email_unique UNIQUE (email)
);