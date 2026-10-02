package com.library.library_management.service.impl;

import com.library.library_management.dto.member.CreateMemberRequest;
import com.library.library_management.dto.member.MemberResponse;
import com.library.library_management.dto.member.UpdateMemberRequest;
import com.library.library_management.entity.AppUser;
import com.library.library_management.entity.Enums;
import com.library.library_management.entity.Member;
import com.library.library_management.entity.MemberHistory;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.DuplicateResourceException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.AppUserRepository;
import com.library.library_management.repository.MemberHistoryRepository;
import com.library.library_management.repository.MemberRepository;
import com.library.library_management.security.SecurityUtils;
import com.library.library_management.service.EventLogger;
import com.library.library_management.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final AppUserRepository appUserRepository;
    private final MemberHistoryRepository memberHistoryRepository;
    private final EventLogger eventLogger;

    @Override
    @Transactional
    public MemberResponse createMember(CreateMemberRequest request) {
        if (memberRepository.existsByNationalCode(request.nationalCode())) {
            throw new DuplicateResourceException("nationalCode", "National code already exists");
        }

        Member member = new Member();
        member.setFullName(request.fullName());
        member.setNationalCode(request.nationalCode());
        member.setBirthDate(request.birthDate());
        member.setMembershipType(request.membershipType());
        member.setPhone(request.phone());
        member.setAddress(request.address());
        member.setPostalCode(request.postalCode());
        member.setMembershipNumber(generateMembershipNumber());
        member.setStatus(Enums.MemberStatus.ACTIVE);
        member.setCreatedAt(LocalDateTime.now());
        member.setCreatedBy(getCurrentUser());

        Member savedMember = memberRepository.save(member);
        eventLogger.info(
                "MEMBER_CREATE_SUCCESS",
                SecurityUtils.getCurrentUsername(),
                "memberId=" + savedMember.getId() + " membershipNumber=" + savedMember.getMembershipNumber()
        );
        return mapToResponse(savedMember);
    }

    @Override
    public MemberResponse getByMembershipNumber(String membershipNumber) {
        Member member = memberRepository.findByMembershipNumber(membershipNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        eventLogger.info("MEMBER_VIEW", SecurityUtils.getCurrentUsername(), "memberId=" + member.getId());
        return mapToResponse(member);
    }

    @Override
    public List<MemberResponse> getAllMembers() {
        List<MemberResponse> result = memberRepository.findAll().stream().map(this::mapToResponse).toList();
        eventLogger.info("MEMBER_LIST", SecurityUtils.getCurrentUsername(), "count=" + result.size());
        return result;
    }

    @Override
    public MemberResponse getByNationalCode(String nationalCode) {
        Member member = memberRepository.findByNationalCode(nationalCode)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        eventLogger.info("MEMBER_VIEW_BY_NATIONAL_CODE", SecurityUtils.getCurrentUsername(), "memberId=" + member.getId());
        return mapToResponse(member);
    }

    @Override
    @Transactional
    public MemberResponse updateMember(Long id, UpdateMemberRequest request) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (request.fullName() != null && !member.getFullName().equals(request.fullName())) {
            saveHistory(member, "fullName", member.getFullName(), request.fullName());
            member.setFullName(request.fullName());
        }

        if (request.phone() != null && !member.getPhone().equals(request.phone())) {
            saveHistory(member, "phone", member.getPhone(), request.phone());
            member.setPhone(request.phone());
        }

        if (request.address() != null && !member.getAddress().equals(request.address())) {
            saveHistory(member, "address", member.getAddress(), request.address());
            member.setAddress(request.address());
        }

        if (request.nationalCode() != null && !member.getNationalCode().equals(request.nationalCode())) {
            if (memberRepository.existsByNationalCode(request.nationalCode())) {
                throw new DuplicateResourceException("nationalCode", "National code already exists");
            }
            saveHistory(member, "nationalCode", member.getNationalCode(), request.nationalCode());
            member.setNationalCode(request.nationalCode());
        }

        if (request.birthDate() != null && !member.getBirthDate().equals(request.birthDate())) {
            saveHistory(member, "birthDate", member.getBirthDate().toString(), request.birthDate().toString());
            member.setBirthDate(request.birthDate());
        }

        if (request.membershipType() != null && !member.getMembershipType().equals(request.membershipType())) {
            saveHistory(member, "membershipType", member.getMembershipType().toString(), request.membershipType().toString());
            member.setMembershipType(request.membershipType());
        }

        if (request.postalCode() != null && !member.getPostalCode().equals(request.postalCode())) {
            saveHistory(member, "postalCode", member.getPostalCode(), request.postalCode());
            member.setPostalCode(request.postalCode());
        }

        if (request.status() != null && member.getStatus() != request.status()) {
            saveHistory(member, "status", member.getStatus().toString(), request.status().toString());
            member.setStatus(request.status());
        }

        member.setUpdatedAt(LocalDateTime.now());
        Member saved = memberRepository.save(member);

        eventLogger.info(
                "MEMBER_UPDATE_SUCCESS",
                SecurityUtils.getCurrentUsername(),
                "memberId=" + saved.getId()
        );
        return mapToResponse(saved);
    }

    private AppUser getCurrentUser() {
        return appUserRepository.findByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private String generateMembershipNumber() {
        String number;
        do {
            number = String.valueOf((long) (Math.random() * 9000000000L + 1000000000L));
        } while (memberRepository.existsByMembershipNumber(number));
        return number;
    }

    private MemberResponse mapToResponse(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getMembershipNumber(),
                member.getFullName(),
                member.getNationalCode(),
                member.getBirthDate(),
                member.getMembershipType(),
                member.getPhone(),
                member.getAddress(),
                member.getPostalCode(),
                member.getStatus(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }

    private void saveHistory(Member member, String fieldName, String oldValue, String newValue) {
        MemberHistory history = new MemberHistory();
        history.setMember(member);
        history.setChangedBy(getCurrentUser());
        history.setFieldName(fieldName);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setChangedAt(LocalDateTime.now());
        memberHistoryRepository.save(history);
    }
}
