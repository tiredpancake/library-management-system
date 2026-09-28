package com.library.library_management.dto.book;

import com.library.library_management.entity.Enums.BookStatus;

import java.math.BigDecimal;

public record UpdateBookRequest(

        String title,
        String author,
        String category,
        String publisher,
        Integer publishYear,
        Integer totalCopies,
        BigDecimal price,
        BookStatus status

) {
}