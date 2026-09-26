package com.altis.library_backend.rentals.models.entities;

import com.altis.library_backend.books.models.entities.BookEntity;
import com.altis.library_backend.publishers.models.entities.PublisherEntity;
import com.altis.library_backend.users.models.entities.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rentals")
@EntityListeners(AuditingEntityListener.class)
public class RentalEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private UserEntity usersId;

    @ManyToOne
    @JoinColumn(name = "books_id")
    private BookEntity booksId;

    @CreationTimestamp
    @Column(name="start_date",nullable = false)
    private LocalDate startDate;

    @Column(name="end_date",nullable = false)
    private LocalDate endDate;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "was_returned", nullable = false)
    private Boolean wasReturned = false;

    @Column(name = "was_renewed", nullable = false)
    private Boolean wasRenewed = false;

    @CreatedDate
    @Column(name="created_at" , nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;
}