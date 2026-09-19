package com.altis.library_backend.books.models.dtos;

import com.altis.library_backend.publishers.models.entities.PublisherEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

public record UpdateRequestDTO(

        String title,

        String genre,

        String releaseDate,

        Long publisherId

) {}
