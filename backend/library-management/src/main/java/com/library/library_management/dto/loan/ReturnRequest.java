package com.library.library_management.dto.loan;

import jakarta.validation.constraints.AssertTrue;

public record ReturnRequest(

        String membershipNumber, String bookCode, String trackingCode) {

    @AssertTrue(message = "Provide either a tracking code or both membership number and book code")
    public boolean isValidReturnIdentifier() {
        boolean hasTrackingCode = trackingCode != null && !trackingCode.isBlank();
        boolean hasMemberAndBook = membershipNumber != null && !membershipNumber.isBlank() && bookCode != null && !bookCode.isBlank();

        return hasTrackingCode || hasMemberAndBook;
    }
}
