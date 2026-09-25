package com.altis.library_backend.books.repositories;


import aj.org.objectweb.asm.commons.Remapper;
import com.altis.library_backend.books.models.entities.BookEntity;
import com.altis.library_backend.publishers.models.entities.PublisherEntity;
import com.altis.library_backend.users.models.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long>, JpaSpecificationExecutor<BookEntity> {

    Optional<BookEntity> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    boolean existsByPublisherId_Id(Long publisherId);

    Page<BookEntity> findByQuantityGreaterThan(Integer quantity, Pageable pageable);
}
