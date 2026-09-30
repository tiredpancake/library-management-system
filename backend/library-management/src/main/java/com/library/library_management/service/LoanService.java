package com.library.library_management.service;

import com.library.library_management.dto.loan.BorrowRequest;
import com.library.library_management.dto.loan.LoanResponse;
import com.library.library_management.dto.loan.RenewRequest;
import com.library.library_management.dto.loan.ReturnRequest;

public interface LoanService {

    LoanResponse borrowBook(BorrowRequest request);

    LoanResponse returnBook(ReturnRequest request);

    LoanResponse renewLoan(RenewRequest request);

}