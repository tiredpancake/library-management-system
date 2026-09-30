package com.library.library_management.dto.member;

import com.library.library_management.entity.Enums.MemberStatus;
import com.library.library_management.entity.Enums.MembershipType;

import java.time.LocalDate;

public record UpdateMemberRequest(

        String fullName,
        String nationalCode,
        LocalDate birthDate,
        MembershipType membershipType,
        String phone,
        String address,
        String postalCode,
        MemberStatus status
) {
}