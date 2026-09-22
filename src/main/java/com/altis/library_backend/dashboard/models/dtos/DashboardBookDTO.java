package com.altis.library_backend.dashboard.models.dtos;

public record DashboardBookDTO(
        Long bookId,
        String title,
        Integer quantity
) {
}