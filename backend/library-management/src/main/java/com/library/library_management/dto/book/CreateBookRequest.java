package com.library.library_management.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateBookRequest(

        @NotBlank(message = "ISBN is required") String isbn,

        @NotBlank String title,

        @NotBlank String author,

        @NotBlank String category,

        @NotBlank String publisher,

        @NotNull(message = "Publish year is required") Integer publishYear,

        @NotNull Integer totalCopies,

        @PositiveOrZero(message = "Price cannot be negative") BigDecimal price

) {
}