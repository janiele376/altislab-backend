package com.altis.library_backend.auth.models.dtos;

public record AuthenticationDTO(
        String email,
        String password
) {}
