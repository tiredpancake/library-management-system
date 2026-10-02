package com.library.library_management.service.impl;

import com.library.library_management.config.LibraryProperties;
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
import com.library.library_management.service.LoanService;
import com.library.library_management.service.validation.LoanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanTransactionRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final AppUserRepository appUserRepository;
    private final FineRepository fineRepository;
    private final LoanValidator loanValidator;
    private final LibraryProperties properties;
    private final EventLogger eventLogger;

    @Override
    @Transactional
    public LoanResponse borrowBook(BorrowRequest request) {
        Member member = memberRepository.findByMembershipNumber(request.membershipNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        Book book = bookRepository.findByBookCode(request.bookCode())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        loanValidator.validateMember(member);
        loanValidator.validateLoanLimit(member);
        loanValidator.validateOverdue(member);
        loanValidator.validateUnpaidFine(member);
        loanValidator.validateBook(book);
        loanValidator.validateNoCurrentLoan(member, book);

        LoanTransaction loan = new LoanTransaction();
        loan.setMember(member);
        loan.setBook(book);
        loan.setCreatedBy(getCurrentUser());
        loan.setType(Enums.LoanType.BORROW);
        loan.setStatus(Enums.LoanStatus.SUCCESS);
        loan.setTrackingCode(generateTrackingCode());

        LocalDateTime now = LocalDateTime.now();
        loan.setRequestDate(now);
        loan.setDueDate(now.plusDays(properties.getLoanPeriodDays()));
        loan.setRenewCount(0);

        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        LoanTransaction savedLoan = loanRepository.save(loan);

        eventLogger.info(
                "LOAN_BORROW_SUCCESS",
                SecurityUtils.getCurrentUsername(),
                "loanId=" + savedLoan.getId()
                        + " memberId=" + member.getId()
                        + " bookId=" + book.getId()
                        + " trackingCode=" + savedLoan.getTrackingCode()
        );

        return mapToResponse(savedLoan);
    }

    @Override
    @Transactional
    public LoanResponse returnBook(ReturnRequest request) {
        LoanTransaction oldLoan;

        if (request.trackingCode() != null && !request.trackingCode().isBlank()) {
            oldLoan = loanRepository.findByTrackingCode(request.trackingCode().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Loan not found"));
        } else {
            Member member = memberRepository.findByMembershipNumber(request.membershipNumber().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

            Book book = bookRepository.findByBookCode(request.bookCode().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Book not found"));

            oldLoan = loanRepository.findFirstByMember_IdAndBook_IdOrderByIdDesc(member.getId(), book.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Current loan not found"));
        }

        if (oldLoan.getReturnDate() != null) {
            throw new BusinessException("Book already returned");
        }

        if (oldLoan.getType() == Enums.LoanType.RETURN) {
            throw new BusinessException("This transaction is already a return transaction");
        }

        if (loanRepository.existsByParentTransactionId(oldLoan.getId())) {
            throw new BusinessException("This loan is no longer the current transaction. Use the latest tracking code.");
        }

        LoanTransaction returnTransaction = new LoanTransaction();
        returnTransaction.setMember(oldLoan.getMember());
        returnTransaction.setBook(oldLoan.getBook());
        returnTransaction.setCreatedBy(getCurrentUser());
        returnTransaction.setType(Enums.LoanType.RETURN);
        returnTransaction.setStatus(Enums.LoanStatus.SUCCESS);
        returnTransaction.setParentTransaction(oldLoan);
        returnTransaction.setTrackingCode(generateTrackingCode());

        LocalDateTime now = LocalDateTime.now();
        returnTransaction.setRequestDate(now);
        returnTransaction.setDueDate(oldLoan.getDueDate());
        returnTransaction.setReturnDate(now);
        returnTransaction.setRenewCount(oldLoan.getRenewCount());

        Book book = oldLoan.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        LoanTransaction savedReturn = loanRepository.save(returnTransaction);
        Fine fine = createFineIfNeeded(savedReturn);

        eventLogger.info(
                "LOAN_RETURN_SUCCESS",
                SecurityUtils.getCurrentUsername(),
                "loanId=" + savedReturn.getId()
                        + " parentLoanId=" + oldLoan.getId()
                        + " memberId=" + savedReturn.getMember().getId()
                        + " bookId=" + savedReturn.getBook().getId()
                        + " trackingCode=" + savedReturn.getTrackingCode()
                        + " overdue=" + (fine != null)
                        + " fineId=" + (fine == null ? "none" : fine.getId())
        );

        return mapToResponse(savedReturn);
    }

    @Override
    @Transactional
    public LoanResponse renewLoan(RenewRequest request) {
        LoanTransaction oldLoan = loanRepository.findByTrackingCode(request.trackingCode())
                .orElseThrow(() -> new BusinessException("Loan not found"));

        if (oldLoan.getReturnDate() != null) {
            throw new BusinessException("Book already returned");
        }

        if (loanRepository.existsByParentTransactionId(oldLoan.getId())) {
            throw new BusinessException("This loan is no longer the current transaction. Use the latest tracking code.");
        }

        if (oldLoan.getRenewCount() >= properties.getMaxRenewCount()) {
            throw new BusinessException("Maximum renew limit reached");
        }

        LoanTransaction renew = new LoanTransaction();
        renew.setMember(oldLoan.getMember());
        renew.setBook(oldLoan.getBook());
        renew.setCreatedBy(getCurrentUser());
        renew.setType(Enums.LoanType.RENEW);
        renew.setStatus(Enums.LoanStatus.SUCCESS);
        renew.setParentTransaction(oldLoan);
        renew.setRenewCount(oldLoan.getRenewCount() + 1);

        LocalDateTime now = LocalDateTime.now();
        renew.setRequestDate(now);
        renew.setDueDate(oldLoan.getDueDate().plusDays(properties.getLoanPeriodDays()));
        renew.setTrackingCode(generateTrackingCode());

        LoanTransaction saved = loanRepository.save(renew);

        eventLogger.info(
                "LOAN_RENEW_SUCCESS",
                SecurityUtils.getCurrentUsername(),
                "loanId=" + saved.getId()
                        + " parentLoanId=" + oldLoan.getId()
                        + " memberId=" + saved.getMember().getId()
                        + " bookId=" + saved.getBook().getId()
                        + " trackingCode=" + saved.getTrackingCode()
                        + " renewCount=" + saved.getRenewCount()
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanResponse getLoanStatus(String trackingCode) {
        if (trackingCode == null || trackingCode.isBlank()) {
            throw new BusinessException("Tracking code is required");
        }

        LoanTransaction loan = loanRepository.findByTrackingCode(trackingCode.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Tracking code not found"));

        eventLogger.info(
                "LOAN_STATUS_VIEW",
                SecurityUtils.getCurrentUsername(),
                "loanId=" + loan.getId() + " trackingCode=" + loan.getTrackingCode()
        );

        return mapToResponse(loan);
    }

    private String generateTrackingCode() {
        return UUID.randomUUID().toString();
    }

    private AppUser getCurrentUser() {
        return appUserRepository.findByUsername(SecurityUtils.getCurrentUsername())
                .orElseThrow(() -> new BusinessException("User not found"));
    }

    private LoanResponse mapToResponse(LoanTransaction loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getTrackingCode(),
                loan.getMember().getMembershipNumber(),
                loan.getBook().getBookCode(),
                loan.getType(),
                loan.getStatus(),
                loan.getRequestDate(),
                loan.getDueDate(),
                loan.getReturnDate(),
                loan.getRenewCount()
        );
    }

    private Fine createFineIfNeeded(LoanTransaction loan) {
        if (fineRepository.findByLoanTransactionId(loan.getId()).isPresent()) {
            return null;
        }

        if (!loan.getReturnDate().isAfter(loan.getDueDate())) {
            return null;
        }

        long lateDays = java.time.Duration.between(
                loan.getDueDate(),
                loan.getReturnDate()
        ).toDays();

        BigDecimal amount = BigDecimal.valueOf(
                Math.min(
                        lateDays * properties.getFinePerDay(),
                        properties.getMaxFine()
                )
        );

        Fine fine = new Fine();
        fine.setLoanTransaction(loan);
        fine.setAmount(amount);
        fine.setStatus(Enums.FineStatus.UNPAID);
        fine.setCreatedAt(LocalDateTime.now());
        fine.setPaidAmount(BigDecimal.ZERO);

        Fine savedFine = fineRepository.save(fine);

        eventLogger.info(
                "FINE_CREATE_SUCCESS",
                SecurityUtils.getCurrentUsername(),
                "fineId=" + savedFine.getId()
                        + " loanId=" + loan.getId()
                        + " lateDays=" + lateDays
                        + " amount=" + amount
        );

        return savedFine;
    }
}
