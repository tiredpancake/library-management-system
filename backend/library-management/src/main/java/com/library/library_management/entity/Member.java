package com.library.library_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "membership_number", nullable = false, unique = true)
    private String membershipNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "national_code", nullable = false, unique = true)
    private String nationalCode;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false)
    private Enums.MembershipType membershipType;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.MemberStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}