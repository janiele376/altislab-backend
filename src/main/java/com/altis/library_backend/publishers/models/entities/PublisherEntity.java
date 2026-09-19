package com.altis.library_backend.publishers.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "publishers")
@EntityListeners(AuditingEntityListener.class)
public class PublisherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="cnpj",nullable = false, unique = true)
    private String cnpj;

    @Column(name="email",nullable = false, unique = true)
    private String email;

    @Column(name="phone",nullable = false)
    private String phone;

    @Column(name="address",nullable = false)
    private String address;

    @CreatedDate
    @Column(name="created_at" , nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name="updated_at", nullable = false)
    private LocalDateTime updatedAt;
}