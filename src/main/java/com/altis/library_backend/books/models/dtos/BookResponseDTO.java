package com.altis.library_backend.books.models.dtos;

import com.altis.library_backend.publishers.models.entities.PublisherEntity;

public record BookResponseDTO(
        Long id,
        String isbn,
        String title,
        String genre,
        String releaseDate,
        Long publisherId,
        String publisherName,
        Integer quantity
) {}
