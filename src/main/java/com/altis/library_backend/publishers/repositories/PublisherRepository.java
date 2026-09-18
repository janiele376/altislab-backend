package com.altis.library_backend.publishers.repositories;


import com.altis.library_backend.publishers.models.entities.Publishers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publishers, Long> {
    Optional<Publishers> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Publishers> findByCnpj(String cnpj);

    boolean existsByCnpj(String cnpj);

    Optional<Publishers> findPublisherById(Long id);
}
