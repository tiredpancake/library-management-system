package com.library.library_management.dto.loan;


import com.library.library_management.entity.Enums;

import java.time.LocalDateTime;


public record LoanHistoryResponse(

        Long id,

        String trackingCode,

        String membershipNumber,

        String bookCode,

        Enums.LoanType type,

        Enums.LoanStatus status,

        LocalDateTime requestDate,

        LocalDateTime dueDate,

        LocalDateTime returnDate

) {
}