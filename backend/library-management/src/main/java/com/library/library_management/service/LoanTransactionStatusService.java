package com.library.library_management.service;

import com.library.library_management.dto.loan.LoanResponse;
import com.library.library_management.entity.Enums;
import com.library.library_management.entity.LoanTransaction;

public interface LoanTransactionStatusService {

    LoanTransaction createPendingTransaction(Enums.LoanType type);

    LoanResponse markFailed(Long transactionId, RuntimeException ex);
}