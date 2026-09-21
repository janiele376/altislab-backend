package com.altis.library_backend.dashboard.models.dtos;

import java.util.List;

public record AdminDashboardResponseDTO(
        Long onTimeRentals,
        Long nearDueRentals,
        Long overdueRentals,
        List<DashboardRentalDTO> lastRentals,
        Long totalRentals,
        Long totalBooks,
        Long totalPublishers,
        String mostRentedBook
) {
}