package com.altis.library_backend.users.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginResponseDTO (
        @Email(message = "the format is invalid")
        @NotBlank(message = "The email field is required")
        String email,

        String token,
        Long expiresIn
){}
