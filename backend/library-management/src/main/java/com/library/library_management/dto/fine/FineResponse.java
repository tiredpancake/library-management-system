package com.library.library_management.dto.fine;

import com.library.library_management.entity.Enums.FineStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record FineResponse(

        Long id,
        Long loanTransactionId,
        BigDecimal amount,
        FineStatus status,
        LocalDateTime createdAt,
        LocalDateTime paidAt
) {
}