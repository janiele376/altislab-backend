package com.altis.library_backend.rentals.specifications;

import com.altis.library_backend.rentals.models.entities.RentalEntity;
import org.springframework.data.jpa.domain.Specification;

public class RentalSpecification {

    public static Specification<RentalEntity> hasUserName(String userName) {
        return (root, query, criteriaBuilder) -> {
            if (userName == null || userName.isBlank()) {
                return null;
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(
                            root.get("usersId").get("nameCompleted")
                    ),
                    "%" + userName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<RentalEntity> hasBookTitle(String bookTitle) {
        return (root, query, criteriaBuilder) -> {
            if (bookTitle == null || bookTitle.isBlank()) {
                return null;
            }

            return criteriaBuilder.like(
                    criteriaBuilder.lower(
                            root.get("booksId").get("title")
                    ),
                    "%" + bookTitle.toLowerCase() + "%"
            );
        };
    }
}