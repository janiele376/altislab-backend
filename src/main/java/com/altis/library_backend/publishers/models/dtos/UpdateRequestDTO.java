package com.altis.library_backend.publishers.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateRequestDTO(
        String name,

        @Email(message = "the format is invalid")
        String email,

        @Pattern(regexp = "^\\(\\d{2}\\)\\s9\\d{4}-\\d{4}$")
        String phone,

        String address
) {}
