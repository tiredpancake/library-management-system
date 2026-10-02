package com.library.library_management.controller;

import com.library.library_management.dto.loan.LoanHistoryResponse;
import com.library.library_management.entity.Enums;
import com.library.library_management.service.LoanHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanHistoryController {

    private final LoanHistoryService loanHistoryService;

    @GetMapping("/history")
    public Page<LoanHistoryResponse> searchHistory(

            @RequestParam(required = false) String membershipNumber,

            @RequestParam(required = false) String bookCode,

            @RequestParam(required = false) Enums.LoanType type,

            @RequestParam(required = false) String state,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,

            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,

            Pageable pageable

    ) {

        return loanHistoryService.searchHistory(membershipNumber, bookCode, type, state, from, to, pageable);
    }
}