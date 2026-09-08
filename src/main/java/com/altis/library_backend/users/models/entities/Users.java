package com.altis.library_backend.users.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false)
    private String nameCompleted;

    @Column(name="email",nullable = false, unique = true)
    private String email;

    @Column(name="phone",nullable = false)
    private String phone;

    @Column(name="cpf",nullable = false, unique = true)
    private String cpf;

    @Column(name="date_birth",nullable = false)
    private LocalDate dateBirth;

    @Column(name="address",nullable = false)
    private String address;

    @Column(name="is_admin",nullable = false)
    private Boolean isAdmin;

    @Column(name="is_disabled", nullable = false)
    private Boolean isDisabled;

    @Column(name="password",nullable = false)
    private String password;

    @CreatedDate
    @Column(name="created_at ", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
