package com.altis.library_backend.rentals.repositories;

import com.altis.library_backend.books.models.entities.BookEntity;
import com.altis.library_backend.rentals.models.entities.RentalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<RentalEntity, Long>, JpaSpecificationExecutor<RentalEntity> {

    boolean existsByUsersId_Id(Long usersId);

    boolean existsByBooksId_Id(Long booksId);

    List<RentalEntity> findByUsersId_Id(Long userId);

    List<RentalEntity> findTop5ByOrderByCreatedAtDesc();

    List<RentalEntity> findTop5ByUsersId_IdOrderByCreatedAtDesc(Long userId);
}