package com.altis.library_backend.rentals.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RentalRequestDTO(
        Long usersId,
        String nameUser,
        Long booksId,
        String titleBook
) {}
