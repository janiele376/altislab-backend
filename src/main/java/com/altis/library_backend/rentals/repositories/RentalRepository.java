package com.altis.library_backend.rentals.repositories;

import com.altis.library_backend.rentals.models.entities.RentalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<RentalEntity, Long> {

    boolean existsByUsersId_Id(Long usersId);

    boolean existsByBooksId_Id(Long booksId);

    List<RentalEntity> findByUsersId_Id(Long userId);

    List<RentalEntity> findTop5ByOrderByCreatedAtDesc();

    List<RentalEntity> findTop5ByUsersId_IdOrderByCreatedAtDesc(Long userId);
}