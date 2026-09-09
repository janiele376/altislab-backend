package com.altis.library_backend.users.models.dtos;

public record RegisterResponseDTO(
    Long id,
    String name,
    String email,
    String cpf,
    String phone,
    String address
) {}
