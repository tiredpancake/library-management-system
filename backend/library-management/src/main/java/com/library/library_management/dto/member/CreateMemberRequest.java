package com.library.library_management.dto.member;

import com.library.library_management.entity.Enums.MembershipType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateMemberRequest(

        @NotBlank
        String fullName,

        @NotBlank
        String nationalCode,

        @NotNull
        LocalDate birthDate,

        @NotNull
        MembershipType membershipType,

        @NotBlank
        String phone,

        @NotBlank
        String address,

        @NotBlank
        String postalCode

) {
}