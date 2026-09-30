package com.library.library_management.dto.loan;

import jakarta.validation.constraints.NotBlank;

public record ReturnRequest(

        @NotBlank
        String trackingCode
) {
}