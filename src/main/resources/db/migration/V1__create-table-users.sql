CREATE TABLE users
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)                        NOT NULL,
    email       VARCHAR(255) UNIQUE                 NOT NULL,
    phone       VARCHAR(20)                         NOT NULL,
    cpf         VARCHAR(14) UNIQUE                  NOT NULL,
    date_birth  DATE                                NOT NULL,
    address     VARCHAR(255)                        NOT NULL,
    is_admin    BOOLEAN   DEFAULT FALSE             NOT NULL,
    is_disabled BOOLEAN   DEFAULT FALSE             NOT NULL,
    password    VARCHAR(255)                        NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);