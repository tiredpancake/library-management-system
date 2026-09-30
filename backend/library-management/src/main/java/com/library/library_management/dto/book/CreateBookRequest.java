package com.library.library_management.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateBookRequest(

        @NotBlank
        String isbn,
        @NotBlank
        String title,
        @NotBlank
        String author,
        @NotBlank
        String category,
        @NotBlank
        String publisher,
        @NotNull
        Integer publishYear,
        @NotNull
        Integer totalCopies,
        BigDecimal price

) {
}