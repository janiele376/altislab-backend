package com.altis.library_backend.dashboard.models.dtos;

import java.time.LocalDate;

public record DashboardRentalDTO(
        Long rentalId,
        Long bookId,
        String title,
        LocalDate startDate,
        LocalDate endDate,
        String status
) {
}