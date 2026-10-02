package com.library.library_management.service;

import com.library.library_management.dto.loan.BorrowRequest;
import com.library.library_management.dto.loan.LoanResponse;
import com.library.library_management.dto.loan.RenewRequest;
import com.library.library_management.dto.loan.ReturnRequest;

public interface LoanOperationService {

    LoanResponse executeBorrow(Long transactionId, BorrowRequest request);

    LoanResponse executeReturn(Long transactionId, ReturnRequest request);

    LoanResponse executeRenew(Long transactionId, RenewRequest request);
}