package com.altis.library_backend.rentals.services;

import com.altis.library_backend.books.models.entities.BookEntity;
import com.altis.library_backend.books.repositories.BookRepository;
import com.altis.library_backend.rentals.models.dtos.RentalRequestDTO;
import com.altis.library_backend.rentals.models.dtos.RentalResponseDTO;
import com.altis.library_backend.rentals.models.dtos.RentalUpdateDTO;
import com.altis.library_backend.rentals.models.entities.RentalEntity;
import com.altis.library_backend.rentals.repositories.RentalRepository;
import com.altis.library_backend.rentals.specifications.RentalSpecification;
import com.altis.library_backend.users.models.entities.UserEntity;
import com.altis.library_backend.users.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public RentalService(
            RentalRepository rentalRepository,
            UserRepository userRepository,
            BookRepository bookRepository
    ) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
    }

    @Transactional(readOnly = true)
    public RentalResponseDTO findById(Long id) {

        RentalEntity findRental = rentalRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Rental not found with ID: " + id
                        )
                );

        return new RentalResponseDTO(
                findRental.getId(),
                findRental.getUsersId().getId(),
                findRental.getBooksId().getId(),
                findRental.getStartDate(),
                findRental.getEndDate(),
                getRentalStatus(findRental)
        );
    }

    @Transactional(readOnly = true)
    public Page<RentalResponseDTO> findAll(
            String userName,
            String bookTitle,
            Pageable pageable
    ) {

        Specification<RentalEntity> spec =
                RentalSpecification.hasUserName(userName)
                        .and(RentalSpecification.hasBookTitle(bookTitle));

        Page<RentalEntity> rentals =
                rentalRepository.findAll(spec, pageable);

        return rentals.map(rental -> new RentalResponseDTO(
                rental.getId(),
                rental.getUsersId().getId(),
                rental.getBooksId().getId(),
                rental.getStartDate(),
                rental.getEndDate(),
                getRentalStatus(rental)
        ));
    }

    @Transactional
    public RentalResponseDTO createRental(RentalRequestDTO request) {

        UserEntity user = userRepository
                .findById(request.usersId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with ID: " + request.usersId()
                        )
                );

        BookEntity book = bookRepository
                .findById(request.booksId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Book not found with ID: " + request.booksId()
                        )
                );

        if (book.getQuantity() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Book is not available for rental."
            );
        }

        if (user.getIsAdmin() == true) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin users cannot rent books."
            );
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(14);

        RentalEntity newRental = new RentalEntity();

        newRental.setUsersId(user);
        newRental.setBooksId(book);
        newRental.setStartDate(startDate);
        newRental.setEndDate(endDate);
        newRental.setStatus("ACTIVE");

        book.setQuantity(book.getQuantity() - 1);
        bookRepository.save(book);

        RentalEntity savedRental = rentalRepository.save(newRental);

        return new RentalResponseDTO(
                savedRental.getId(),
                savedRental.getUsersId().getId(),
                savedRental.getBooksId().getId(),
                savedRental.getStartDate(),
                savedRental.getEndDate(),
                getRentalStatus(savedRental)
        );
    }

    @Transactional
    public RentalResponseDTO updateRental(Long id, RentalUpdateDTO request) {

        RentalEntity rental = rentalRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Rental not found with ID: " + id
                        )
                );

        UserEntity user = userRepository
                .findById(request.usersId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with ID: " + request.usersId()
                        )
                );

        if (request.usersId() == null && request.booksId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Please provide the user ID or book ID you want to edit."
            );
        }

        if (user.getIsAdmin() == true) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Admin users cannot rent books."
            );
        }

        Long currentUserId = rental.getUsersId().getId();
        Long currentBookId = rental.getBooksId().getId();

        boolean sameUser = request.usersId() == null || request.usersId().equals(currentUserId);

        boolean sameBook = request.booksId() == null || request.booksId().equals(currentBookId);

        if (sameUser && sameBook) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Data is the same as the current rental. Please choose what you want to edit."
            );
        }

        if (request.usersId() != null) { rental.setUsersId(user); }

        if (request.booksId() != null) {

            BookEntity newBook = bookRepository.findById(request.booksId())
                    .orElseThrow(() ->
                            new ResponseStatusException(
                                    HttpStatus.NOT_FOUND,
                                    "Book not found with ID: " + request.booksId()
                            )
                    );

            if (newBook.getQuantity() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Book is not available for rental."
                );
            }

            BookEntity oldBook = rental.getBooksId();

            oldBook.setQuantity(oldBook.getQuantity() + 1);
            newBook.setQuantity(newBook.getQuantity() - 1);

            bookRepository.save(oldBook);
            bookRepository.save(newBook);

            rental.setBooksId(newBook);
        }

        RentalEntity savedRental = rentalRepository.save(rental);

        return new RentalResponseDTO(
                savedRental.getId(),
                savedRental.getUsersId().getId(),
                savedRental.getBooksId().getId(),
                savedRental.getStartDate(),
                savedRental.getEndDate(),
                getRentalStatus(savedRental)
        );
    }

    @Transactional
    public RentalResponseDTO renewRental(Long id) {

        RentalEntity existingRental = rentalRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Rental not found with ID: " + id
                        )
                );

        existingRental.setEndDate(
                existingRental.getEndDate().plusDays(14)
        );

        RentalEntity savedRental = rentalRepository.save(existingRental);

        return new RentalResponseDTO(
                savedRental.getId(),
                savedRental.getUsersId().getId(),
                savedRental.getBooksId().getId(),
                savedRental.getStartDate(),
                savedRental.getEndDate(),
                getRentalStatus(savedRental)
        );
    }

    @Transactional
    public RentalResponseDTO returnRental(Long id) {

        RentalEntity existingRental = rentalRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Rental not found with ID: " + id
                        )
                );

        if (existingRental.getStatus().equals("RETURNED")) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Rental has already been returned."
            );
        }

        existingRental.setStatus("RETURNED");

        BookEntity book = existingRental.getBooksId();

        book.setQuantity(
                book.getQuantity() + 1
        );

        bookRepository.save(book);

        RentalEntity savedRental =
                rentalRepository.save(existingRental);

        return new RentalResponseDTO(
                savedRental.getId(),
                savedRental.getUsersId().getId(),
                savedRental.getBooksId().getId(),
                savedRental.getStartDate(),
                savedRental.getEndDate(),
                getRentalStatus(savedRental)
        );
    }

    private String getRentalStatus(RentalEntity rental) {

        if (rental.getStatus().equals("RETURNED")) {
            return "RETURNED";
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = rental.getEndDate();

        if (today.isAfter(endDate)) {
            return "LATE";
        }

        if (!today.isBefore(endDate.minusDays(2))) {
            return "DUE_SOON";
        }

        return "ACTIVE";
    }

    @Transactional
    public void deleteRental(Long id) {

        RentalEntity rental = rentalRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Rental not found with ID: " + id
                        )
                );

        if (rental.getUsersId() != null || rental.getBooksId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This rental cannot be deleted because it is connected to a user or book."
            );
        }

        rentalRepository.delete(rental);
    }
}