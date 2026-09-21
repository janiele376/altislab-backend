package com.altis.library_backend.dashboard.models.dtos;

import java.util.List;

public record UserDashboardResponseDTO(
        List<DashboardRentalDTO> lastRentals,
        Integer availableBooks,
        Long onTimeRentals,
        Long nearDueRentals,
        Long overdueRentals,
        String mostRentedBook
) {
}