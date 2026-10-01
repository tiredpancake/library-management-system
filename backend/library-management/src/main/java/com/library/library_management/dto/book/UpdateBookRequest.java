package com.library.library_management.dto.book;

import com.library.library_management.entity.Enums.BookStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateBookRequest(

        String title,
        String author,
        String category,
        String publisher,
        Integer publishYear,
        @PositiveOrZero(
                message = "Total copies cannot be negative"
        )
        Integer totalCopies,
        @PositiveOrZero(
                message = "Price cannot be negative"
        )
        BigDecimal price,
        BookStatus status

) {
}