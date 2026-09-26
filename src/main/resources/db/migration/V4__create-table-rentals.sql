CREATE TABLE rentals
(
    id           BIGSERIAL PRIMARY KEY,
    users_id     BIGSERIAL                           NOT NULL,
    books_id     BIGSERIAL                           NOT NULL,
    start_date   DATE                                NOT NULL,
    end_date     DATE                                NOT NULL,
    status       VARCHAR(150)                        NOT NULL,
    was_returned BOOLEAN                             NOT NULL,
    was_renewed  BOOLEAN                             NOT NULL,
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,

    CONSTRAINT fk_rentals_users FOREIGN KEY (users_id)
        REFERENCES users (id),
    CONSTRAINT fk_rentals_books FOREIGN KEY (books_id)
        REFERENCES books (id)
);