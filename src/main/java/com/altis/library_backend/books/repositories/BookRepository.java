package com.altis.library_backend.books.repositories;


import com.altis.library_backend.books.models.entities.BookEntity;
import com.altis.library_backend.publishers.models.entities.PublisherEntity;
import com.altis.library_backend.users.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    Optional<BookEntity> findByIsbn(String isbn);

    boolean existsByIsbn(String isbn);

    boolean existsByTitle(String title);

    boolean existsByGenre(String genre);

    boolean existsByReleaseDate(String releaseDate);

    boolean existsByPublisherId_Id(Long publisherId);

}
