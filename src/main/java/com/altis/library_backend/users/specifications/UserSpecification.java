package com.altis.library_backend.users.specifications;

import com.altis.library_backend.books.models.entities.BookEntity;
import com.altis.library_backend.users.models.entities.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {

    public static Specification<UserEntity> isNotAdmin() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.isFalse(root.get("isAdmin"));
    }

    public static Specification<UserEntity> hasName(String nameCompleted) {

        return (root, query, criteriaBuilder) -> {
            if(nameCompleted == null || nameCompleted.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("nameCompleted")), "%"+nameCompleted.toLowerCase()+"%");
        };
    }

    public static Specification<UserEntity> hasEmail(String email) {

        return (root, query, criteriaBuilder) -> {
            if(email == null || email.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%"+email.toLowerCase()+"%");
        };
    }

    public static Specification<UserEntity> hasCpf(String cpf) {

        return (root, query, criteriaBuilder) -> {
            if(cpf == null || cpf.isBlank()) {
                return null;
            }
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("cpf")), "%"+cpf.toLowerCase()+"%");
        };
    }

    public static Specification<UserEntity> hasDisabled(Boolean isDisabled) {
        return (root, query, criteriaBuilder) -> {
            if (isDisabled == null) {
                return null;
            }

            return criteriaBuilder.equal(root.get("isDisabled"), isDisabled);
        };
    }
}
