package com.library.library_management.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "fine")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "loan_transaction_id",
            nullable = false,
            unique = true
    )
    private LoanTransaction loanTransaction;


    @Column(nullable = false)
    private Double amount;


    @Column(name = "paid_amount", nullable = false)
    private Double paidAmount;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Enums.FineStatus status;


    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;


    private LocalDateTime paidAt;
}