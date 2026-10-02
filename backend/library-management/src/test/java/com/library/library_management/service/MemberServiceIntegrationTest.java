package com.library.library_management.service;

import com.library.library_management.dto.member.CreateMemberRequest;
import com.library.library_management.dto.member.MemberResponse;
import com.library.library_management.dto.member.UpdateMemberRequest;
import com.library.library_management.entity.AppUser;
import com.library.library_management.entity.Enums;
import com.library.library_management.entity.Member;
import com.library.library_management.entity.MemberHistory;
import com.library.library_management.exception.DuplicateResourceException;
import com.library.library_management.repository.AppUserRepository;
import com.library.library_management.repository.MemberHistoryRepository;
import com.library.library_management.repository.MemberRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MemberServiceIntegrationTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberHistoryRepository memberHistoryRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser testUser;

    @BeforeEach
    void setUp() {

        String username = "member-test-" + UUID.randomUUID();

        testUser = new AppUser();
        testUser.setUsername(username);
        testUser.setPassword("test-password");

        testUser = appUserRepository.save(testUser);

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList()));
    }

    @Test
    void createMemberShouldGenerateTenDigitMembershipNumber() {

        CreateMemberRequest request = createRequest(generateNumericString(10));

        MemberResponse response = memberService.createMember(request);

        assertNotNull(response.id());

        assertNotNull(response.membershipNumber());

        assertTrue(response.membershipNumber().matches("\\d{10}"));

        assertEquals(Enums.MemberStatus.ACTIVE, response.status());

        assertEquals(request.nationalCode(), response.nationalCode());

        assertTrue(memberRepository.existsByMembershipNumber(response.membershipNumber()));
    }

    @Test
    void generatedMembershipNumbersShouldBeUnique() {

        MemberResponse first = memberService.createMember(createRequest(generateNumericString(10)));

        MemberResponse second = memberService.createMember(createRequest(generateNumericString(10)));

        assertNotEquals(first.membershipNumber(), second.membershipNumber());

        assertTrue(first.membershipNumber().matches("\\d{10}"));

        assertTrue(second.membershipNumber().matches("\\d{10}"));
    }

    @Test
    void duplicateNationalCodeShouldBeRejected() {

        String nationalCode = generateNumericString(10);

        memberService.createMember(createRequest(nationalCode));

        assertThrows(DuplicateResourceException.class, () -> memberService.createMember(new CreateMemberRequest("Second Member", nationalCode, LocalDate.of(1999, 5, 10), Enums.MembershipType.INDIVIDUAL, "09121111111", "Second Address", "2222222222")));

        long count = memberRepository.findAll().stream().filter(member -> nationalCode.equals(member.getNationalCode())).count();

        assertEquals(1, count);
    }

    @Test
    void updateMemberShouldCreateHistoryForChangedFields() {

        MemberResponse created = memberService.createMember(createRequest(generateNumericString(10)));

        UpdateMemberRequest update = new UpdateMemberRequest("Updated Member Name", null, null, null, "09129999999", "Updated Address", null, Enums.MemberStatus.INACTIVE);

        MemberResponse updated = memberService.updateMember(created.id(), update);

        assertEquals("Updated Member Name", updated.fullName());

        assertEquals("09129999999", updated.phone());

        assertEquals("Updated Address", updated.address());

        assertEquals(Enums.MemberStatus.INACTIVE, updated.status());

        assertNotNull(updated.updatedAt());

        List<MemberHistory> history = memberHistoryRepository.findByMemberId(created.id());

        assertEquals(4, history.size());

        assertTrue(history.stream().anyMatch(h -> h.getFieldName().equals("fullName") && h.getOldValue().equals("Test Member") && h.getNewValue().equals("Updated Member Name")));

        assertTrue(history.stream().anyMatch(h -> h.getFieldName().equals("phone") && h.getOldValue().equals("09120000000") && h.getNewValue().equals("09129999999")));

        assertTrue(history.stream().anyMatch(h -> h.getFieldName().equals("address") && h.getOldValue().equals("Test Address") && h.getNewValue().equals("Updated Address")));

        assertTrue(history.stream().anyMatch(h -> h.getFieldName().equals("status") && h.getOldValue().equals("ACTIVE") && h.getNewValue().equals("INACTIVE")));

        assertTrue(history.stream().allMatch(h -> h.getChangedAt() != null));
    }

    @Test
    void unchangedFieldsShouldNotCreateHistory() {

        MemberResponse created = memberService.createMember(createRequest(generateNumericString(10)));

        UpdateMemberRequest update = new UpdateMemberRequest(created.fullName(), null, null, null, created.phone(), created.address(), created.postalCode(), created.status());

        memberService.updateMember(created.id(), update);

        List<MemberHistory> history = memberHistoryRepository.findByMemberId(created.id());

        assertTrue(history.isEmpty());
    }

    @Test
    void updatingNationalCodeToExistingValueShouldBeRejected() {

        MemberResponse first = memberService.createMember(createRequest(generateNumericString(10)));

        MemberResponse second = memberService.createMember(createRequest(generateNumericString(10)));

        String originalSecondNationalCode = second.nationalCode();

        UpdateMemberRequest update = new UpdateMemberRequest(null, first.nationalCode(), null, null, null, null, null, null);

        assertThrows(DuplicateResourceException.class, () -> memberService.updateMember(second.id(), update));

        Member unchanged = memberRepository.findById(second.id()).orElseThrow();

        assertEquals(originalSecondNationalCode, unchanged.getNationalCode());

        List<MemberHistory> history = memberHistoryRepository.findByMemberId(second.id());

        assertTrue(history.isEmpty());
    }

    @Test
    void memberShouldBeRetrievableByMembershipNumberAndNationalCode() {

        MemberResponse created = memberService.createMember(createRequest(generateNumericString(10)));

        MemberResponse byMembershipNumber = memberService.getByMembershipNumber(created.membershipNumber());

        MemberResponse byNationalCode = memberService.getByNationalCode(created.nationalCode());

        assertEquals(created.id(), byMembershipNumber.id());

        assertEquals(created.id(), byNationalCode.id());

        assertEquals(created.membershipNumber(), byNationalCode.membershipNumber());
    }

    private CreateMemberRequest createRequest(String nationalCode) {

        return new CreateMemberRequest("Test Member", nationalCode, LocalDate.of(2000, 1, 1), Enums.MembershipType.INDIVIDUAL, "09120000000", "Test Address", "1234567890");
    }

    private String generateNumericString(int length) {

        String digits = UUID.randomUUID().toString().replaceAll("\\D", "");

        while (digits.length() < length) {

            digits += UUID.randomUUID().toString().replaceAll("\\D", "");
        }

        return digits.substring(0, length);
    }
}