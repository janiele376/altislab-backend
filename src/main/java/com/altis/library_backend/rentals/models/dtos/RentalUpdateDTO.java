package com.altis.library_backend.rentals.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RentalUpdateDTO(
        Long usersId,
        Long booksId
) {}
