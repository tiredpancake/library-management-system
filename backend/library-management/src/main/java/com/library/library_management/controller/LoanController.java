package com.library.library_management.controller;

import com.library.library_management.dto.loan.BorrowRequest;
import com.library.library_management.dto.loan.LoanResponse;
import com.library.library_management.dto.loan.RenewRequest;
import com.library.library_management.dto.loan.ReturnRequest;
import com.library.library_management.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/borrow")
    public LoanResponse borrowBook(@RequestBody @Valid BorrowRequest request) {

        return loanService.borrowBook(request);
    }


    @PostMapping("/return")
    public LoanResponse returnBook(@RequestBody @Valid ReturnRequest request) {

        return loanService.returnBook(request);
    }


    @PostMapping("/renew")
    public LoanResponse renewLoan(@RequestBody @Valid RenewRequest request) {

        return loanService.renewLoan(request);
    }

}