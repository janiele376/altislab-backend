package com.altis.library_backend.users.repositories;

import com.altis.library_backend.users.models.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Users> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    Optional<Users> findUserById(Long id);


}
