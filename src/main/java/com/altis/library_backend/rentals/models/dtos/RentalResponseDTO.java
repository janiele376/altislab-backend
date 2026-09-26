package com.altis.library_backend.rentals.models.dtos;

import java.time.LocalDate;

public record RentalResponseDTO(
        Long id,
        Long usersId,
        Long booksId,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {}
