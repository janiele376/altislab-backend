package com.altis.library_backend.publishers.models.dtos;

public record UpdateResponseDTO(
        Long id,
        String name,
        String email,
        String phone,
        String address
) {
}
