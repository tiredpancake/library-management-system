package com.library.library_management.controller;

import com.library.library_management.dto.member.CreateMemberRequest;
import com.library.library_management.dto.member.MemberResponse;
import com.library.library_management.dto.member.UpdateMemberRequest;
import com.library.library_management.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {


    private final MemberService memberService;


    @PostMapping
    public MemberResponse createMember(
            @RequestBody @Valid CreateMemberRequest request
    ) {

        return memberService.createMember(request);
    }



    @GetMapping("/membership/{number}")
    public MemberResponse getByMembershipNumber(
            @PathVariable String number
    ) {

        return memberService.getByMembershipNumber(number);
    }



    @GetMapping("/national-code/{code}")
    public MemberResponse getByNationalCode(
            @PathVariable String code
    ) {

        return memberService.getByNationalCode(code);
    }



    @PutMapping("/{id}")
    public MemberResponse updateMember(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMemberRequest request
    ) {

        return memberService.updateMember(id, request);
    }

    @GetMapping
    public List<MemberResponse> getAllMembers(){

        return memberService.getAllMembers();

    }
}