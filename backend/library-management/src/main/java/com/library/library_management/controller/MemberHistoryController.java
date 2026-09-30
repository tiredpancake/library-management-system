package com.library.library_management.controller;


import com.library.library_management.dto.history.MemberHistoryResponse;
import com.library.library_management.service.MemberHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberHistoryController {

    private final MemberHistoryService memberHistoryService;

    @GetMapping("/{memberId}/history")
    public List<MemberHistoryResponse> getHistory(@PathVariable Long memberId) {

        return memberHistoryService.getMemberHistory(memberId);
    }

}