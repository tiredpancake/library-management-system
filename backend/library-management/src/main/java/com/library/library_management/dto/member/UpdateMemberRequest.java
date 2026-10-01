package com.library.library_management.dto.member;


import com.library.library_management.entity.Enums.MemberStatus;
import com.library.library_management.entity.Enums.MembershipType;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;



public record UpdateMemberRequest(


        String fullName,
        @Pattern(
                regexp = "\\d{10}",
                message = "National code must be exactly 10 digits"
        )
        String nationalCode,
        @Past(
                message = "Birth date must be before today"
        )
        LocalDate birthDate,
        MembershipType membershipType,


        @Pattern(
                regexp = "\\d{11}",
                message = "Phone must be exactly 11 digits"
        )
        String phone,
        String address,


        @Pattern(
                regexp = "\\d{10}",
                message = "Postal code must be exactly 10 digits"
        )
        String postalCode,
        MemberStatus status


) {

}