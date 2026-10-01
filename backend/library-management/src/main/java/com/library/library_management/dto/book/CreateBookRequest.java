package com.library.library_management.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateBookRequest(

        @NotBlank(message = "ISBN is required")
        @Pattern(
                regexp = "\\d{14}",
                message = "ISBN must be exactly 14 digits"
        )
        String isbn,

        @NotBlank(message = "Title is required")
        String title,
        @NotBlank(message = "Author is required")
        String author,
        @NotBlank(message = "Category is required")
        String category,
        @NotBlank(message = "Publisher is required")
        String publisher,
        @NotNull(message = "Publish year is required")
        Integer publishYear,
        @NotNull(message = "Total copies is required")
        @PositiveOrZero(
                message = "Total copies cannot be negative"
        )
        Integer totalCopies,
        @PositiveOrZero(
                message = "Price cannot be negative"
        )
        BigDecimal price

) {
}