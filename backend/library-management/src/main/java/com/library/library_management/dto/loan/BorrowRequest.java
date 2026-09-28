package com.library.library_management.dto.loan;

import jakarta.validation.constraints.NotBlank;

public record BorrowRequest(

        @NotBlank
        String membershipNumber,

        @NotBlank
        String bookCode

) {
}