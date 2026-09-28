package com.library.library_management.dto.loan;

import jakarta.validation.constraints.NotBlank;

public record RenewRequest(

        @NotBlank
        String trackingCode

) {
}