CREATE TABLE books
(
    id           BIGSERIAL PRIMARY KEY,
    title        VARCHAR(255)                        NOT NULL,
    isbn         VARCHAR(255)                        NOT NULL UNIQUE,
    genre        VARCHAR(150)                        NOT NULL,
    quantity     INTEGER                             NOT NULL,
    publisher_id BIGSERIAL                             NOT NULL,
    release_date DATE                                NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

        CONSTRAINT fk_books_publishers FOREIGN KEY (publisher_id)
        REFERENCES publishers(id)
);