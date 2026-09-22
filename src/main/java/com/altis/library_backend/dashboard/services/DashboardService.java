package com.altis.library_backend.dashboard.services;

import com.altis.library_backend.books.models.entities.BookEntity;
import com.altis.library_backend.books.repositories.BookRepository;
import com.altis.library_backend.dashboard.models.dtos.AdminDashboardResponseDTO;
import com.altis.library_backend.dashboard.models.dtos.DashboardBookDTO;
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

        return new UserDashboardResponseDTO(
                toDashboardRentalDTOList(lastRentals),
                countRentalsByStatus(userRentals, "ON_TIME"),
                countRentalsByStatus(userRentals, "NEAR_DUE"),
                countRentalsByStatus(userRentals, "OVERDUE"),
                findMostRentedBook(userRentals),
                getAvailableBooks()
        );
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponseDTO getAdminDashboard() {

        List<RentalEntity> rentals = rentalRepository.findAll();

        List<RentalEntity> lastRentals = rentalRepository.findTop5ByOrderByCreatedAtDesc();

        return new AdminDashboardResponseDTO(
                countRentalsByStatus(rentals, "ON_TIME"),
                countRentalsByStatus(rentals, "NEAR_DUE"),
                countRentalsByStatus(rentals, "OVERDUE"),
                toDashboardRentalDTOList(lastRentals),
                rentalRepository.count(),
                bookRepository.count(),
                publisherRepository.count(),
                findMostRentedBook(rentals),
                getAvailableBooks()
        );
    }

    private long countRentalsByStatus(List<RentalEntity> rentals, String expectedStatus) {
        return rentals.stream()
                .filter(rental ->
                        calculateStatus(rental).equals(expectedStatus)
                )
                .count();
    }

    private List<DashboardRentalDTO> toDashboardRentalDTOList(List<RentalEntity> rentals) {
        return rentals.stream()
                .map(this::toDashboardRentalDTO)
                .toList();
    }

    private List<DashboardBookDTO> getAvailableBooks() {

        return bookRepository.findAll()
                .stream()
                .filter(book ->
                        book.getQuantity() != null
                                && book.getQuantity() > 0
                )
                .map(this::toDashboardBookDTO)
                .toList();
    }

    private DashboardBookDTO toDashboardBookDTO(BookEntity book) {
        return new DashboardBookDTO(
                book.getId(),
                book.getTitle(),
                book.getQuantity()
        );
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

        for (Map.Entry<BookEntity, Long> entry : bookCount.entrySet()) {

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