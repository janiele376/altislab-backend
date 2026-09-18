package com.altis.library_backend.users.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginResponseDTO (
        String email,

        String token,
        Long expiresIn
){}
