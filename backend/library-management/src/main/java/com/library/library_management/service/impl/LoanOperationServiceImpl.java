package com.library.library_management.service.impl;

import com.library.library_management.config.LibraryProperties;
import com.library.library_management.dto.loan.BorrowRequest;
import com.library.library_management.dto.loan.LoanResponse;
import com.library.library_management.dto.loan.RenewRequest;
import com.library.library_management.dto.loan.ReturnRequest;
import com.library.library_management.entity.*;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.BookRepository;
import com.library.library_management.repository.FineRepository;
import com.library.library_management.repository.LoanTransactionRepository;
import com.library.library_management.repository.MemberRepository;
import com.library.library_management.security.SecurityUtils;
import com.library.library_management.service.EventLogger;
import com.library.library_management.service.LoanOperationService;
import com.library.library_management.service.validation.LoanValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanOperationServiceImpl implements LoanOperationService {

    private final LoanTransactionRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final FineRepository fineRepository;
    private final LoanValidator loanValidator;
    private final LibraryProperties properties;
    private final EventLogger eventLogger;

    @Override
    @Transactional
    public LoanResponse executeBorrow(Long transactionId, BorrowRequest request) {

        LoanTransaction loan = getPendingTransaction(transactionId);

        Member member = memberRepository
                .findByMembershipNumberForUpdate(request.membershipNumber().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));
        Book book = bookRepository.findByBookCodeForUpdate(request.bookCode().trim()).orElseThrow(() -> new ResourceNotFoundException("Book not found"));

        loan.setMember(member);
        loan.setBook(book);

        loanValidator.validateMember(member);
        loanValidator.validateLoanLimit(member);
        loanValidator.validateOverdue(member);
        loanValidator.validateUnpaidFine(member);
        loanValidator.validateBook(book);
        loanValidator.validateNoCurrentLoan(member, book);

        LocalDateTime now = LocalDateTime.now();

        loan.setStatus(Enums.LoanStatus.SUCCESS);
        loan.setRequestDate(now);
        loan.setDueDate(now.plusDays(properties.getLoanPeriodDays()));
        loan.setRenewCount(0);
        loan.setErrorMessage(null);

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        bookRepository.save(book);
        LoanTransaction savedLoan = loanRepository.save(loan);

        eventLogger.info("LOAN_BORROW_SUCCESS", SecurityUtils.getCurrentUsername(), "loanId=" + savedLoan.getId() + " memberId=" + member.getId() + " bookId=" + book.getId() + " trackingCode=" + savedLoan.getTrackingCode());

        return mapToResponse(savedLoan);
    }

    @Override
    @Transactional
    public LoanResponse executeReturn(Long transactionId, ReturnRequest request) {

        LoanTransaction returnTransaction = getPendingTransaction(transactionId);

        LoanTransaction oldLoan;

        if (request.trackingCode() != null && !request.trackingCode().isBlank()) {

            oldLoan = loanRepository.findByTrackingCodeForUpdate(request.trackingCode().trim()).orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        } else {

            Member member = memberRepository
                    .findByMembershipNumberForUpdate(request.membershipNumber().trim())
                    .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

            Book book = bookRepository.findByBookCode(request.bookCode().trim()).orElseThrow(() -> new ResourceNotFoundException("Book not found"));

            oldLoan = loanRepository.findLatestByMemberAndBookForUpdate(member.getId(), book.getId()).orElseThrow(() -> new ResourceNotFoundException("Current loan not found"));
        }

        returnTransaction.setMember(oldLoan.getMember());
        returnTransaction.setBook(oldLoan.getBook());

        if (oldLoan.getStatus() != Enums.LoanStatus.SUCCESS) {
            throw new BusinessException("This loan transaction is not successful");
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

        returnTransaction.setParentTransaction(oldLoan);

        LocalDateTime now = LocalDateTime.now();

        returnTransaction.setStatus(Enums.LoanStatus.SUCCESS);
        returnTransaction.setRequestDate(now);
        returnTransaction.setDueDate(oldLoan.getDueDate());
        returnTransaction.setReturnDate(now);
        returnTransaction.setRenewCount(oldLoan.getRenewCount());
        returnTransaction.setErrorMessage(null);

        Book book = oldLoan.getBook();

        book.setAvailableCopies(book.getAvailableCopies() + 1);

        bookRepository.save(book);

        LoanTransaction savedReturn = loanRepository.save(returnTransaction);

        Fine fine = createFineIfNeeded(savedReturn);

        eventLogger.info("LOAN_RETURN_SUCCESS", SecurityUtils.getCurrentUsername(), "loanId=" + savedReturn.getId() + " parentLoanId=" + oldLoan.getId() + " memberId=" + savedReturn.getMember().getId() + " bookId=" + savedReturn.getBook().getId() + " trackingCode=" + savedReturn.getTrackingCode() + " overdue=" + (fine != null) + " fineId=" + (fine == null ? "none" : fine.getId()));

        return mapToResponse(savedReturn);
    }

    @Override
    @Transactional
    public LoanResponse executeRenew(Long transactionId, RenewRequest request) {

        LoanTransaction renew = getPendingTransaction(transactionId);

        LoanTransaction oldLoan = loanRepository.findByTrackingCode(request.trackingCode().trim()).orElseThrow(() -> new ResourceNotFoundException("Loan not found"));

        renew.setMember(oldLoan.getMember());
        renew.setBook(oldLoan.getBook());

        if (oldLoan.getStatus() != Enums.LoanStatus.SUCCESS) {
            throw new BusinessException("This loan transaction is not successful");
        }

        if (oldLoan.getReturnDate() != null) {
            throw new BusinessException("Book already returned");
        }

        if (oldLoan.getType() == Enums.LoanType.RETURN) {
            throw new BusinessException("A returned transaction cannot be renewed");
        }

        if (loanRepository.existsByParentTransactionId(oldLoan.getId())) {
            throw new BusinessException("This loan is no longer the current transaction. Use the latest tracking code.");
        }

        if (oldLoan.getRenewCount() >= properties.getMaxRenewCount()) {
            throw new BusinessException("Maximum renew limit reached");
        }

        loanValidator.validateMember(oldLoan.getMember());
        loanValidator.validateBookActive(oldLoan.getBook());

        renew.setParentTransaction(oldLoan);
        renew.setRenewCount(oldLoan.getRenewCount() + 1);

        LocalDateTime now = LocalDateTime.now();

        renew.setStatus(Enums.LoanStatus.SUCCESS);
        renew.setRequestDate(now);
        renew.setDueDate(oldLoan.getDueDate().plusDays(properties.getLoanPeriodDays()));
        renew.setErrorMessage(null);

        LoanTransaction saved = loanRepository.save(renew);

        eventLogger.info("LOAN_RENEW_SUCCESS", SecurityUtils.getCurrentUsername(), "loanId=" + saved.getId() + " parentLoanId=" + oldLoan.getId() + " memberId=" + saved.getMember().getId() + " bookId=" + saved.getBook().getId() + " trackingCode=" + saved.getTrackingCode() + " renewCount=" + saved.getRenewCount());

        return mapToResponse(saved);
    }

    private LoanTransaction getPendingTransaction(Long transactionId) {

        LoanTransaction transaction = loanRepository.findById(transactionId).orElseThrow(() -> new ResourceNotFoundException("Loan transaction not found"));

        if (transaction.getStatus() != Enums.LoanStatus.PENDING) {
            throw new BusinessException("Loan transaction is not pending");
        }

        return transaction;
    }

    private Fine createFineIfNeeded(LoanTransaction loan) {

        if (fineRepository.findByLoanTransactionId(loan.getId()).isPresent()) {
            return null;
        }

        if (!loan.getReturnDate().isAfter(loan.getDueDate())) {
            return null;
        }

        long lateMinutes = java.time.Duration
                .between(loan.getDueDate(), loan.getReturnDate())
                .toMinutes();

        long lateDays = (lateMinutes + 1439) / 1440;


        BigDecimal amount = BigDecimal.valueOf(Math.min(lateDays * properties.getFinePerDay(), properties.getMaxFine()));

        Fine fine = new Fine();

        fine.setLoanTransaction(loan);
        fine.setAmount(amount);
        fine.setStatus(Enums.FineStatus.UNPAID);
        fine.setCreatedAt(LocalDateTime.now());
        fine.setPaidAmount(BigDecimal.ZERO);

        Fine savedFine = fineRepository.save(fine);

        eventLogger.info("FINE_CREATE_SUCCESS", SecurityUtils.getCurrentUsername(), "fineId=" + savedFine.getId() + " loanId=" + loan.getId() + " lateDays=" + lateDays + " amount=" + amount);

        return savedFine;
    }

    private LoanResponse mapToResponse(LoanTransaction loan) {

        return new LoanResponse(loan.getId(), loan.getTrackingCode(), loan.getMember() == null ? null : loan.getMember().getMembershipNumber(), loan.getBook() == null ? null : loan.getBook().getBookCode(), loan.getType(), loan.getStatus(), loan.getRequestDate(), loan.getDueDate(), loan.getReturnDate(), loan.getRenewCount(), loan.getErrorMessage());
    }
}