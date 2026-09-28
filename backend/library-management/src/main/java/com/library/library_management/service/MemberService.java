package com.library.library_management.service;

import com.library.library_management.dto.member.CreateMemberRequest;
import com.library.library_management.dto.member.MemberResponse;
import com.library.library_management.dto.member.UpdateMemberRequest;

public interface MemberService {

    MemberResponse createMember(CreateMemberRequest request);

    MemberResponse getByMembershipNumber(String membershipNumber);

    MemberResponse getByNationalCode(String nationalCode);

    MemberResponse updateMember(
            Long id,
            UpdateMemberRequest request
    );
}