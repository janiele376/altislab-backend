package com.altis.library_backend.rentals.services;

import com.altis.library_backend.books.models.entities.BookEntity;
import com.altis.library_backend.books.repositories.BookRepository;
import com.altis.library_backend.rentals.models.dtos.RentalRequestDTO;
import com.altis.library_backend.rentals.models.dtos.RentalResponseDTO;
import com.altis.library_backend.rentals.models.entities.RentalEntity;
import com.altis.library_backend.rentals.repositories.RentalRepository;
import com.altis.library_backend.users.models.entities.UserEntity;
import com.altis.library_backend.users.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
                        new IllegalArgumentException(
                                "Rental not found with ID: " + id
                        )
                );

        return new RentalResponseDTO(
                findRental.getId(),
                findRental.getUsersId().getId(),
                findRental.getUsersId().getNameCompleted(),
                findRental.getBooksId().getId(),
                findRental.getBooksId().getTitle(),
                findRental.getStartDate(),
                findRental.getEndDate(),
                getRentalStatus(findRental)
        );
    }

    @Transactional(readOnly = true)
    public List<RentalResponseDTO> findAll() {

        List<RentalEntity> rentals = rentalRepository.findAll();
        List<RentalResponseDTO> responses = new ArrayList<>();

        for (RentalEntity rental : rentals) {

            RentalResponseDTO response = new RentalResponseDTO(
                    rental.getId(),
                    rental.getUsersId().getId(),
                    rental.getUsersId().getNameCompleted(),
                    rental.getBooksId().getId(),
                    rental.getBooksId().getTitle(),
                    rental.getStartDate(),
                    rental.getEndDate(),
                    getRentalStatus(rental)
            );

            responses.add(response);
        }

        return responses;
    }

    @Transactional
    public RentalResponseDTO createRental(RentalRequestDTO request) {

        UserEntity user = userRepository
                .findById(request.usersId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "User not found with ID: " + request.usersId()
                        )
                );

        BookEntity book = bookRepository
                .findById(request.booksId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Book not found with ID: " + request.booksId()
                        )
                );

        if (book.getQuantity() <= 0) {
            throw new IllegalArgumentException(
                    "Book is not available for rental."
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
                savedRental.getUsersId().getNameCompleted(),
                savedRental.getBooksId().getId(),
                savedRental.getBooksId().getTitle(),
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
                        new IllegalArgumentException(
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
                savedRental.getUsersId().getNameCompleted(),
                savedRental.getBooksId().getId(),
                savedRental.getBooksId().getTitle(),
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
                        new IllegalArgumentException(
                                "Rental not found with ID: " + id
                        )
                );

        if (existingRental.getStatus().equals("RETURNED")) {
            throw new IllegalArgumentException(
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
                savedRental.getUsersId().getNameCompleted(),
                savedRental.getBooksId().getId(),
                savedRental.getBooksId().getTitle(),
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

        if (!today.isBefore(endDate.minusDays(3))) {
            return "DUE_SOON";
        }

        return "ACTIVE";
    }
}