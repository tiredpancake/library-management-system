package com.library.library_management.dto.member;


import com.library.library_management.entity.Enums.MembershipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;


public record CreateMemberRequest(


        @NotBlank(message = "Full name is required")
        String fullName,


        @NotBlank(message = "National code is required")
        @Pattern(
                regexp = "\\d{10}",
                message = "National code must be exactly 10 digits"
        )
        String nationalCode,


        @NotNull(message = "Birth date is required")
        @Past(message = "Birth date must be before today")
        LocalDate birthDate,


        @NotNull(message = "Membership type is required")
        MembershipType membershipType,


        @NotBlank(message = "Phone is required")
        @Pattern(
                regexp = "\\d{11}",
                message = "Phone must be exactly 11 digits"
        )
        String phone,


        @NotBlank(message = "Address is required")
        String address,


        @NotBlank(message = "Postal code is required")
        @Pattern(
                regexp = "\\d{10}",
                message = "Postal code must be exactly 10 digits"
        )
        String postalCode


) {

}