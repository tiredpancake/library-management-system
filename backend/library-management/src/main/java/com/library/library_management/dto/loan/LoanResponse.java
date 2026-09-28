package com.library.library_management.dto.loan;

import com.library.library_management.entity.Enums.LoanStatus;
import com.library.library_management.entity.Enums.LoanType;

import java.time.LocalDateTime;

public record LoanResponse(

        Long id,

        String trackingCode,

        String membershipNumber,

        String bookCode,

        LoanType type,

        LoanStatus status,

        LocalDateTime requestDate,

        LocalDateTime dueDate,

        LocalDateTime returnDate,

        Integer renewCount

) {
}