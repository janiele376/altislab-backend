package com.altis.library_backend.books.specifications;

import com.altis.library_backend.books.models.entities.BookEntity;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<BookEntity> hasTitle(String title) {

        return (root, query, criteriaBuilder) -> {
            if(title == null || title.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), "%"+title.toLowerCase()+"%");
        };
    }

    public static Specification<BookEntity> hasGenre(String genre) {

        return (root, query, criteriaBuilder) -> {
            if(genre == null || genre.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("genre")), "%"+genre.toLowerCase()+"%");
        };
    }

    public static Specification<BookEntity> hasIsbn(String isbn) {

        return (root, query, criteriaBuilder) -> {
            if(isbn == null || isbn.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("isbn")), "%"+isbn.toLowerCase()+"%");
        };
    }
}