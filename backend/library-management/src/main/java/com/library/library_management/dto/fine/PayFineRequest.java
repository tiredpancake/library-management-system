package com.library.library_management.dto.fine;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public record PayFineRequest(

        String membershipNumber,
        @DecimalMin(value = "0.01")
        BigDecimal amount

) {}