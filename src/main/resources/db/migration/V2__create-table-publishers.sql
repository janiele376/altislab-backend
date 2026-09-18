CREATE TABLE publishers
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(255)                        NOT NULL,
    cnpj       VARCHAR(255)                        NOT NULL UNIQUE,
    email      VARCHAR(255)                        NOT NULL UNIQUE,
    phone      VARCHAR(255)                        NOT NULL,
    address    VARCHAR(255)                        NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);