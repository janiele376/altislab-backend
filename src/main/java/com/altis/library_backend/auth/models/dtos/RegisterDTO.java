package com.altis.library_backend.auth.models.dtos;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record RegisterDTO(
        String nameCompleted,
        String email,
        String phone,
        String address,
        String cpf,
        LocalDate dateBirth,
        String password
) {}
