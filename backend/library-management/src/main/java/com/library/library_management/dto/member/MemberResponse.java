package com.library.library_management.dto.member;

import com.library.library_management.entity.Enums.MemberStatus;
import com.library.library_management.entity.Enums.MembershipType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record MemberResponse(

        Long id,

        String membershipNumber,

        String fullName,

        String nationalCode,

        LocalDate birthDate,

        MembershipType membershipType,

        String phone,

        String address,

        String postalCode,

        MemberStatus status,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {
}