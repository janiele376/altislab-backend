package com.altis.library_backend.publishers.models.dtos;

public record PublisherResponseDTO(
        Long id,
        String name,
        String cnpj,
        String email,
        String phone,
        String address
) {}
