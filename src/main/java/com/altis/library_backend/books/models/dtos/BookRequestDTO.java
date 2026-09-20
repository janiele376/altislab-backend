package com.altis.library_backend.books.models.dtos;

import com.altis.library_backend.publishers.models.entities.PublisherEntity;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public record BookRequestDTO(

        @NotBlank
        @Size(max = 13)
        String isbn,

        @NotBlank
        String title,

        @NotBlank
        String genre,

        @NotBlank
        String releaseDate,

        Long publisherId,

        String publisherName

) {}
