package com.altis.library_backend.users.models.dtos;

import java.time.LocalDate;

public record UserResponseDTO(
    Long id,
    String nameCompleted,
    String email,
    String phone,
    String cpf,
    LocalDate dateBirth,
    String address
) {}
