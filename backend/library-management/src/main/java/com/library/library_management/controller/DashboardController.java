package com.library.library_management.controller;

import com.library.library_management.dto.dashboard.DashboardResponse;
import com.library.library_management.entity.Enums;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.FineRepository;
import com.library.library_management.repository.LoanTransactionRepository;
import com.library.library_management.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final LoanTransactionRepository loanRepository;
    private final FineRepository fineRepository;

    @GetMapping("/summary")
    public DashboardResponse summary() {
        return new DashboardResponse(bookRepository.count(), memberRepository.count(), loanRepository.countByStatusAndReturnDateIsNull(Enums.LoanStatus.SUCCESS), fineRepository.countByStatus(Enums.FineStatus.UNPAID));
    }
}