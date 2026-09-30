package com.library.library_management.dto.book;

import com.library.library_management.entity.Enums.BookStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookResponse(

        Long id,
        String bookCode,
        String isbn,
        String title,
        String author,
        String category,
        String publisher,
        Integer publishYear,
        Integer totalCopies,
        Integer availableCopies,
        BigDecimal price,
        BookStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}