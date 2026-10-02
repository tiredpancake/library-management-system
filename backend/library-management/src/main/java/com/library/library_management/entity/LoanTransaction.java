package com.library.library_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "loan_transaction",
        indexes = {
                @Index(name = "idx_loan_member_id", columnList = "member_id"),
                @Index(name = "idx_loan_book_id", columnList = "book_id"),
                @Index(name = "idx_loan_request_date", columnList = "request_date"),
                @Index(name = "idx_loan_parent_transaction", columnList = "parent_transaction_id", unique = true)
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_loan_parent_transaction",
                        columnNames = "parent_transaction_id"
                )
        }
)
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
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
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
    private Integer renewCount;


    private String errorMessage;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_transaction_id", unique = true)
    private LoanTransaction parentTransaction;
}