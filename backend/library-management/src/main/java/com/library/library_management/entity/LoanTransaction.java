package com.library.library_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "loan_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "tracking_code", nullable = false, unique = true)
    private String trackingCode;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private AppUser createdBy;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.LoanType type;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.LoanStatus status;


    @Column(name = "request_date", nullable = false)
    private LocalDateTime requestDate;


    private LocalDateTime dueDate;


    private LocalDateTime returnDate;


    @Column(nullable = false)
    private Integer renewCount = 0;


    private String errorMessage;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_loan_id")
    private LoanTransaction relatedLoan;
}