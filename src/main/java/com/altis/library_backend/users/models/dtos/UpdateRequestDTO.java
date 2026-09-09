package com.altis.library_backend.users.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateRequestDTO (
        @Size(min = 3, max = 150, message = "The name must be between 3 and 150 characters long.")
        @Pattern(
                regexp = "^[a-zA-ZÀ-ÿ]+(?:\\s+[a-zA-ZÀ-ÿ]+)+$",
                message = "Enter your full name (first and last name), containing only letters."
        )
        String name,

        @Email(message = "the format is invalid")
        String email,

        String address,

        @Pattern(regexp = "^\\(\\d{2}\\)\\s9\\d{4}-\\d{4}$", message = "The phone must follow the pattern (99) 99999-9999.")
        String phone,

        @NotBlank(message = "The current password is required to confirm changes.")
        @Size(min = 8, message = "The password field require 8 characters")
        String currentPassword
){}
