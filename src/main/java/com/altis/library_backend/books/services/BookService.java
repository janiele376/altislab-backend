package com.altis.library_backend.books.services;

import com.altis.library_backend.books.models.dtos.BookRequestDTO;
import com.altis.library_backend.books.models.dtos.BookResponseDTO;
import com.altis.library_backend.books.models.dtos.UpdateRequestDTO;
import com.altis.library_backend.books.models.dtos.UpdateResponseDTO;
import com.altis.library_backend.books.models.entities.BookEntity;
import com.altis.library_backend.books.repositories.BookRepository;
import com.altis.library_backend.publishers.models.entities.PublisherEntity;
import com.altis.library_backend.publishers.repositories.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;

    public BookService(
            BookRepository bookRepository,
            PublisherRepository publisherRepository
    ) {
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
    }

    @Transactional(readOnly = true)
    public BookResponseDTO findById(Long id) {

        BookEntity findBook = bookRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found with ID: " + id)
                );

        return new BookResponseDTO(
                findBook.getId(),
                findBook.getIsbn(),
                findBook.getTitle(),
                findBook.getGenre(),
                findBook.getReleaseDate(),
                findBook.getPublisherId().getId(),
                findBook.getQuantity()
        );
    }

    @Transactional(readOnly = true)
    public List<BookResponseDTO> findAll() {

        List<BookEntity> books = bookRepository.findAll();

        List<BookResponseDTO> responses = new ArrayList<>();

        for (BookEntity book : books) {

            BookResponseDTO response = new BookResponseDTO(
                    book.getId(),
                    book.getIsbn(),
                    book.getTitle(),
                    book.getGenre(),
                    book.getReleaseDate(),
                    book.getPublisherId().getId(),
                    book.getQuantity()
            );

            responses.add(response);
        }

        return responses;
    }

    @Transactional
    public BookResponseDTO createBook(BookRequestDTO request) {

        if (bookRepository.existsByIsbn(request.isbn())) {

            BookEntity existingBook = bookRepository.findByIsbn(request.isbn())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Book not found")
                    );

            existingBook.setQuantity(
                    existingBook.getQuantity() + 1
            );

            BookEntity savedBook = bookRepository.save(existingBook);

            return new BookResponseDTO(
                    savedBook.getId(),
                    savedBook.getIsbn(),
                    savedBook.getTitle(),
                    savedBook.getGenre(),
                    savedBook.getReleaseDate(),
                    savedBook.getPublisherId().getId(),
                    savedBook.getQuantity()
            );
        }

        PublisherEntity publisher = publisherRepository
                .findById(request.publisherId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Publisher not found with ID: " + request.publisherId()
                        )
                );

        BookEntity newBook = new BookEntity();

        newBook.setIsbn(request.isbn());
        newBook.setTitle(request.title());
        newBook.setGenre(request.genre());
        newBook.setReleaseDate(request.releaseDate());
        newBook.setPublisherId(publisher);

        BookEntity savedBook = bookRepository.save(newBook);

        return new BookResponseDTO(
                savedBook.getId(),
                savedBook.getIsbn(),
                savedBook.getTitle(),
                savedBook.getGenre(),
                savedBook.getReleaseDate(),
                savedBook.getPublisherId().getId(),
                savedBook.getQuantity()
        );
    }

    @Transactional
    public UpdateResponseDTO updateBook(Long id, UpdateRequestDTO request) {

        BookEntity existingBook = bookRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Book not found with ID: " + id)
                );

        if (request.title() != null && !request.title().isBlank()) {
            existingBook.setTitle(request.title());
        }

        if (request.genre() != null && !request.genre().isBlank()) {
            existingBook.setGenre(request.genre());
        }

        if (request.releaseDate() != null) {
            existingBook.setReleaseDate(request.releaseDate());
        }

        if (request.publisherId() != null) {

            PublisherEntity publisher = publisherRepository
                    .findById(request.publisherId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Publisher not found with ID: " + request.publisherId()
                            )
                    );

            existingBook.setPublisherId(publisher);
        }

        BookEntity savedBook = bookRepository.save(existingBook);

        return new UpdateResponseDTO(
                savedBook.getId(),
                savedBook.getTitle(),
                savedBook.getGenre(),
                savedBook.getReleaseDate(),
                savedBook.getPublisherId().getId()
        );
    }

    @Transactional
    public void deleteBook(Long id) {

        if (!bookRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Book not found with ID: " + id
            );
        }

        bookRepository.deleteById(id);
    }
}