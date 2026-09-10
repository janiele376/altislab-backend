package com.altis.library_backend.users.models.dtos;

public record UpdateResponseDTO(
        Long id,
        String nameCompleted,
        String email,
        String phone,
        String address
) {}
