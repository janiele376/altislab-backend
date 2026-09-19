package com.altis.library_backend.books.models.dtos;

import com.altis.library_backend.publishers.models.entities.PublisherEntity;

public record UpdateResponseDTO(
        Long id,
        String title,
        String genre,
        String releaseDate,
        Long publisherId
) {}
