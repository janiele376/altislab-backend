package com.altis.library_backend.auth.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequestDTO(

        @NotBlank(message = "The email is required")
        @Email(message = "The email format is invalid")
        String email,

        @NotBlank(message = "The CPF is required")
        @Pattern(
                regexp = "^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$",
                message = "The CPF must follow the pattern 000.000.000-00"
        )
        String cpf,

        @NotBlank(message = "The new password is required")
        @Size(min = 8, message = "The password requires at least 8 characters")
        String newPassword,

        @NotBlank(message = "The password confirmation is required")
        String confirmPassword

) {}