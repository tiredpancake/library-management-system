package com.library.library_management.service;

import com.library.library_management.dto.loan.*;

public interface LoanService {

    LoanResponse borrowBook(BorrowRequest request);

    LoanResponse returnBook(ReturnRequest request);

    LoanResponse renewLoan(RenewRequest request);

    LoanResponse getLoanStatus(String trackingCode);

}
