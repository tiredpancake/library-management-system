package com.library.library_management.dto.fine;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


public record PayFineRequest(

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal amount

) {
}