package com.library.library_management.service.impl;

import com.library.library_management.dto.loan.LoanResponse;
import com.library.library_management.entity.AppUser;
import com.library.library_management.entity.Enums;
import com.library.library_management.entity.LoanTransaction;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.AppUserRepository;
import com.library.library_management.repository.LoanTransactionRepository;
import com.library.library_management.security.SecurityUtils;
import com.library.library_management.service.EventLogger;
import com.library.library_management.service.LoanTransactionStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanTransactionStatusServiceImpl implements LoanTransactionStatusService {

    private final LoanTransactionRepository loanRepository;
    private final AppUserRepository appUserRepository;
    private final EventLogger eventLogger;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LoanTransaction createPendingTransaction(Enums.LoanType type) {

        LoanTransaction transaction = new LoanTransaction();

        transaction.setType(type);
        transaction.setStatus(Enums.LoanStatus.PENDING);
        transaction.setTrackingCode(UUID.randomUUID().toString());
        transaction.setRequestDate(LocalDateTime.now());
        transaction.setRenewCount(0);
        transaction.setCreatedBy(getCurrentUser());

        return loanRepository.save(transaction);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public LoanResponse markFailed(Long transactionId, RuntimeException ex) {

        LoanTransaction transaction =
                loanRepository.findById(transactionId)
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Loan transaction not found"
                                )
                        );

        transaction.setStatus(
                Enums.LoanStatus.FAILED
        );

        String publicErrorMessage;

        if (ex instanceof BusinessException
                || ex instanceof ResourceNotFoundException) {

            publicErrorMessage =
                    ex.getMessage() != null
                            ? ex.getMessage()
                            : "Transaction failed";

        } else {

            publicErrorMessage =
                    "Transaction failed due to an internal error";
        }

        if (publicErrorMessage.length() > 255) {
            publicErrorMessage =
                    publicErrorMessage.substring(0, 255);
        }

        transaction.setErrorMessage(
                publicErrorMessage
        );

        LoanTransaction failed =
                loanRepository.save(transaction);

        eventLogger.error(
                "LOAN_TRANSACTION_FAILED",
                SecurityUtils.getCurrentUsername(),
                "loanId=" + failed.getId()
                        + " type=" + failed.getType()
                        + " trackingCode=" + failed.getTrackingCode()
                        + " exception="
                        + ex.getClass().getSimpleName(),
                ex
        );

        return mapToResponse(failed);
    }

    private AppUser getCurrentUser() {
        return appUserRepository.findByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private LoanResponse mapToResponse(LoanTransaction loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getTrackingCode(),
                loan.getMember() == null ? null : loan.getMember().getMembershipNumber(),
                loan.getBook() == null ? null : loan.getBook().getBookCode(),
                loan.getType(),
                loan.getStatus(),
                loan.getRequestDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getRenewCount(),
                loan.getErrorMessage()
        );
    }
}