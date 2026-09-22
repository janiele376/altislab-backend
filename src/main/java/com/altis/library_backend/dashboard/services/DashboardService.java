package com.altis.library_backend.dashboard.services;

import com.altis.library_backend.books.models.entities.BookEntity;
import com.altis.library_backend.books.repositories.BookRepository;
import com.altis.library_backend.dashboard.models.dtos.AdminDashboardResponseDTO;
import com.altis.library_backend.dashboard.models.dtos.DashboardRentalDTO;
import com.altis.library_backend.dashboard.models.dtos.UserDashboardResponseDTO;
import com.altis.library_backend.publishers.repositories.PublisherRepository;
import com.altis.library_backend.rentals.models.entities.RentalEntity;
import com.altis.library_backend.rentals.repositories.RentalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final RentalRepository rentalRepository;
    private final BookRepository bookRepository;
    private final PublisherRepository publisherRepository;

    public DashboardService(
            RentalRepository rentalRepository,
            BookRepository bookRepository,
            PublisherRepository publisherRepository
    ) {
        this.rentalRepository = rentalRepository;
        this.bookRepository = bookRepository;
        this.publisherRepository = publisherRepository;
    }

    @Transactional(readOnly = true)
    public UserDashboardResponseDTO getUserDashboard(Long userId) {

        List<RentalEntity> userRentals = rentalRepository.findByUsersId_Id(userId);

        List<RentalEntity> lastRentals = rentalRepository.findTop5ByUsersId_IdOrderByCreatedAtDesc(userId);

        long onTimeRentals = 0;
        long nearDueRentals = 0;
        long overdueRentals = 0;

        for (RentalEntity rental : userRentals) {

            if (Boolean.TRUE.equals(rental.getWasReturned())) {continue;}

            String status = calculateStatus(rental);

            if (status.equals("ON_TIME")) {onTimeRentals++;}

            if (status.equals("NEAR_DUE")) {nearDueRentals++;}

            if (status.equals("OVERDUE")) {overdueRentals++;}
        }

        List<DashboardRentalDTO> lastRentalsDTO =
                lastRentals.stream()
                        .map(this::toDashboardRentalDTO)
                        .toList();

        String mostRentedBook = findMostRentedBook(userRentals);

        return new UserDashboardResponseDTO(
                lastRentalsDTO,
                onTimeRentals,
                nearDueRentals,
                overdueRentals,
                mostRentedBook
        );
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponseDTO getAdminDashboard() {

        List<RentalEntity> rentals = rentalRepository.findAll();

        List<RentalEntity> lastRentals = rentalRepository.findTop5ByOrderByCreatedAtDesc();

        long onTimeRentals = 0;
        long nearDueRentals = 0;
        long overdueRentals = 0;

        for (RentalEntity rental : rentals) {

            if (Boolean.TRUE.equals(rental.getWasReturned())) {continue;}

            String status = calculateStatus(rental);

            if (status.equals("ON_TIME")) {onTimeRentals++;}

            if (status.equals("NEAR_DUE")) {nearDueRentals++;}

            if (status.equals("OVERDUE")) {overdueRentals++;}
        }

        List<DashboardRentalDTO> lastRentalsDTO =
                lastRentals.stream()
                        .map(this::toDashboardRentalDTO)
                        .toList();

        String mostRentedBook = findMostRentedBook(rentals);

        return new AdminDashboardResponseDTO(
                onTimeRentals,
                nearDueRentals,
                overdueRentals,
                lastRentalsDTO,
                rentalRepository.count(),
                bookRepository.count(),
                publisherRepository.count(),
                mostRentedBook
        );
    }

    private String calculateStatus(RentalEntity rental) {

        if (Boolean.TRUE.equals(rental.getWasReturned())) {
            return "RETURNED";
        }

        LocalDate today = LocalDate.now();
        LocalDate endDate = rental.getEndDate();

        if (endDate.isBefore(today)) {
            return "OVERDUE";
        }

        if (!endDate.isAfter(today.plusDays(2))) {
            return "NEAR_DUE";
        }

        return "ON_TIME";
    }

    private DashboardRentalDTO toDashboardRentalDTO(RentalEntity rental) {

        return new DashboardRentalDTO(
                rental.getId(),
                rental.getBooksId().getId(),
                rental.getBooksId().getTitle(),
                rental.getStartDate(),
                rental.getEndDate(),
                calculateStatus(rental)
        );
    }

    private String findMostRentedBook(List<RentalEntity> rentals) {

        if (rentals.isEmpty()) {
            return null;
        }

        Map<BookEntity, Long> bookCount = new HashMap<>();

        for (RentalEntity rental : rentals) {

            BookEntity book = rental.getBooksId();

            bookCount.put(
                    book,
                    bookCount.getOrDefault(book, 0L) + 1
            );
        }

        BookEntity mostRentedBook = null;
        long highestCount = 0;

        for (Map.Entry<BookEntity, Long> entry
                : bookCount.entrySet()) {

            if (entry.getValue() > highestCount) {

                highestCount = entry.getValue();
                mostRentedBook = entry.getKey();
            }
        }

        if (mostRentedBook == null) {
            return null;
        }

        return mostRentedBook.getTitle();
    }
}