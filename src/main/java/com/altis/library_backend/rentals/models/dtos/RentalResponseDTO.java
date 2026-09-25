package com.altis.library_backend.rentals.models.dtos;

import java.time.LocalDate;

public record RentalResponseDTO(
        Long id,
        Long usersId,
        String nameUser,
        Long booksId,
        String titleBook,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {}
