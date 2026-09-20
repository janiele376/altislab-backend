package com.altis.library_backend.rentals.repositories;


import com.altis.library_backend.rentals.models.entities.RentalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<RentalEntity, Long> {

    boolean existsByUsersId_Id(Long usersId);

    boolean existsByBooksId_Id(Long booksId);

}
