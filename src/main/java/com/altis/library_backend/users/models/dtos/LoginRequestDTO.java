package com.altis.library_backend.users.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(
        @Email(message = "the format is invalid")
        @NotBlank(message = "The email field is required")
        String email,

        @NotBlank(message = "The password field is required")
        @Size(min = 8, message = "The password field require 8 characters")
        String password
) {}
