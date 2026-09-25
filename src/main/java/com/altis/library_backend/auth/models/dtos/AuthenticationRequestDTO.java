package com.altis.library_backend.auth.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthenticationRequestDTO(
        @Email(message = "The format email is invalid")
        @NotBlank(message = "The input is not null")
        String email,

        @NotBlank(message = "The input is not null")
        String password
) {}
