package com.library.library_management.service.impl;

import com.library.library_management.dto.loan.BorrowRequest;
import com.library.library_management.dto.loan.LoanResponse;
import com.library.library_management.dto.loan.RenewRequest;
import com.library.library_management.dto.loan.ReturnRequest;
import com.library.library_management.entity.*;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.*;
import com.library.library_management.security.SecurityUtils;
import com.library.library_management.service.EventLogger;
import com.library.library_management.service.LoanOperationService;
import com.library.library_management.service.LoanService;
import com.library.library_management.service.LoanTransactionStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanTransactionRepository loanRepository;
    private final EventLogger eventLogger;
    private final LoanTransactionStatusService loanTransactionStatusService;
    private final LoanOperationService loanOperationService;



    @Override
    public LoanResponse borrowBook(BorrowRequest request) {

        LoanTransaction pending = loanTransactionStatusService.createPendingTransaction(Enums.LoanType.BORROW);

        try {
            return loanOperationService.executeBorrow(pending.getId(), request);

        } catch (RuntimeException ex) {
            return loanTransactionStatusService.markFailed(pending.getId(), ex);
        }
    }

    @Override
    public LoanResponse returnBook(ReturnRequest request) {

        LoanTransaction pending =
                loanTransactionStatusService.createPendingTransaction(
                        Enums.LoanType.RETURN
                );

        try {
            return loanOperationService.executeReturn(
                    pending.getId(),
                    request
            );

        } catch (RuntimeException ex) {
            return loanTransactionStatusService.markFailed(
                    pending.getId(),
                    ex
            );
        }
    }

    @Override
    public LoanResponse renewLoan(RenewRequest request) {

        LoanTransaction pending =
                loanTransactionStatusService.createPendingTransaction(
                        Enums.LoanType.RENEW
                );

        try {
            return loanOperationService.executeRenew(
                    pending.getId(),
                    request
            );

        } catch (RuntimeException ex) {
            return loanTransactionStatusService.markFailed(
                    pending.getId(),
                    ex
            );
        }
    }


    @Override
    @Transactional(readOnly = true)
    public LoanResponse getLoanStatus(String trackingCode) {

        if (trackingCode == null || trackingCode.isBlank()) {

            throw new BusinessException("Tracking code is required");
        }

        LoanTransaction loan = loanRepository.findByTrackingCode(trackingCode.trim()).orElseThrow(() -> new ResourceNotFoundException("Tracking code not found"));

        eventLogger.info("LOAN_STATUS_VIEW", SecurityUtils.getCurrentUsername(), "loanId=" + loan.getId() + " trackingCode=" + loan.getTrackingCode());

        return mapToResponse(loan);
    }

    private LoanResponse mapToResponse(LoanTransaction loan) {

        return new LoanResponse(loan.getId(), loan.getTrackingCode(), loan.getMember() == null ? null : loan.getMember().getMembershipNumber(), loan.getBook() == null ? null : loan.getBook().getBookCode(), loan.getType(), loan.getStatus(), loan.getRequestDate(), loan.getDueDate(), loan.getReturnDate(), loan.getRenewCount(), loan.getErrorMessage());
    }

}