package com.library.library_management.service.impl;

import com.library.library_management.config.LibraryProperties;
import com.library.library_management.dto.loan.*;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.*;
import com.library.library_management.service.LoanService;
import com.library.library_management.service.validation.LoanValidator;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.library.library_management.entity.*;
import com.library.library_management.entity.Enums;
import com.library.library_management.security.SecurityUtils;

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



    @Override
    @Transactional
    public LoanResponse borrowBook(BorrowRequest request) {
        Member member = memberRepository
                .findByMembershipNumber(request.membershipNumber())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Member not found")
                );


        Book book = bookRepository
                .findByBookCode(request.bookCode())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Book not found")
                );



        loanValidator.validateMember(member);
        loanValidator.validateLoanLimit(member);
        loanValidator.validateOverdue(member);
        loanValidator.validateUnpaidFine(member);
        loanValidator.validateBook(book);


        LoanTransaction loan = new LoanTransaction();


        loan.setMember(member);

        loan.setBook(book);

        loan.setCreatedBy(
                getCurrentUser()
        );


        loan.setType(
                Enums.LoanType.BORROW
        );


        loan.setStatus(
                Enums.LoanStatus.SUCCESS
        );


        loan.setTrackingCode(
                generateTrackingCode()
        );


        loan.setRequestDate(
                LocalDateTime.now()
        );


        loan.setDueDate(
                LocalDateTime.now()
                        .plusDays(
                                properties.getLoanPeriodDays()
                        )
        );


        loan.setRenewCount(0);

        book.setAvailableCopies(
                book.getAvailableCopies() - 1
        );
        bookRepository.save(book);

        LoanTransaction savedLoan =
                loanRepository.save(loan);

        return mapToResponse(savedLoan);

    }



    @Override
    @Transactional
    public LoanResponse returnBook(ReturnRequest request) {

        LoanTransaction oldLoan =
                loanRepository
                        .findByTrackingCode(request.trackingCode())
                        .orElseThrow(
                                () -> new ResourceNotFoundException(
                                        "Loan not found"
                                )
                        );


        if (oldLoan.getReturnDate() != null) {

            throw new BusinessException(
                    "Book already returned"
            );
        }


        LoanTransaction returnTransaction =
                new LoanTransaction();


        returnTransaction.setMember(
                oldLoan.getMember()
        );


        returnTransaction.setBook(
                oldLoan.getBook()
        );


        returnTransaction.setCreatedBy(
                getCurrentUser()
        );


        returnTransaction.setType(
                Enums.LoanType.RETURN
        );


        returnTransaction.setStatus(
                Enums.LoanStatus.SUCCESS
        );


        returnTransaction.setParentTransaction(
                oldLoan
        );


        returnTransaction.setTrackingCode(
                generateTrackingCode()
        );


        returnTransaction.setRequestDate(
                LocalDateTime.now()
        );


        returnTransaction.setDueDate(
                oldLoan.getDueDate()
        );


        returnTransaction.setReturnDate(
                LocalDateTime.now()
        );


        returnTransaction.setRenewCount(
                oldLoan.getRenewCount()
        );


        Book book = oldLoan.getBook();

        book.setAvailableCopies(
                book.getAvailableCopies() + 1
        );

        bookRepository.save(book);



        LoanTransaction savedReturn =
                loanRepository.save(returnTransaction);



        createFineIfNeeded(
                savedReturn
        );


        return mapToResponse(savedReturn);
    }


    @Override
    @Transactional
    public LoanResponse renewLoan(RenewRequest request) {

        LoanTransaction oldLoan =
                loanRepository
                        .findByTrackingCode(request.trackingCode())
                        .orElseThrow(
                                () -> new BusinessException(
                                        "Loan not found"
                                )
                        );


        if (oldLoan.getReturnDate() != null) {

            throw new BusinessException(
                    "Book already returned"
            );
        }


        if (oldLoan.getRenewCount() >= properties.getMaxRenewCount()) {

            throw new BusinessException(
                    "Maximum renew limit reached"
            );
        }


        LoanTransaction renew = new LoanTransaction();


        renew.setMember(
                oldLoan.getMember()
        );


        renew.setBook(
                oldLoan.getBook()
        );


        renew.setCreatedBy(
                getCurrentUser()
        );


        renew.setType(
                Enums.LoanType.RENEW
        );


        renew.setStatus(
                Enums.LoanStatus.SUCCESS
        );


        renew.setParentTransaction(
                oldLoan
        );


        renew.setRenewCount(
                oldLoan.getRenewCount() + 1
        );


        renew.setRequestDate(
                LocalDateTime.now()
        );


        renew.setDueDate(
                oldLoan.getDueDate()
                        .plusDays(14)
        );


        renew.setTrackingCode(
                generateTrackingCode()
        );


        LoanTransaction saved =
                loanRepository.save(renew);


        return mapToResponse(saved);
    }

    private String generateTrackingCode() {

        return UUID.randomUUID()
                .toString();

    }

    private AppUser getCurrentUser() {

        return appUserRepository
                .findByUsername(
                        SecurityUtils.getCurrentUsername()
                )
                .orElseThrow(
                        () -> new BusinessException("User not found")
                );
    }

    private LoanResponse mapToResponse(LoanTransaction loan) {

        return new LoanResponse(

                loan.getId(),

                loan.getTrackingCode(),

                loan.getMember()
                        .getMembershipNumber(),

                loan.getBook()
                        .getBookCode(),

                loan.getType(),

                loan.getStatus(),

                loan.getRequestDate(),

                loan.getDueDate(),

                loan.getReturnDate(),

                loan.getRenewCount()
        );
    }
    private void createFineIfNeeded(LoanTransaction loan) {

        if(fineRepository.findByLoanTransactionId(loan.getId()).isPresent()){
            return;
        }

        if (loan.getReturnDate()
                .isAfter(loan.getDueDate())) {


            long lateDays =
                    java.time.Duration
                            .between(
                                    loan.getDueDate(),
                                    loan.getReturnDate()
                            )
                            .toDays();


            BigDecimal amount =
                    BigDecimal.valueOf(
                            Math.min(
                                    lateDays * properties.getFinePerDay(),
                                    properties.getMaxFine()
                            )
                    );


            Fine fine = new Fine();

            fine.setLoanTransaction(loan);

            fine.setAmount(amount);

            fine.setStatus(
                    Enums.FineStatus.UNPAID
            );

            fine.setCreatedAt(
                    LocalDateTime.now()
            );

            fine.setPaidAmount(BigDecimal.ZERO);
            fineRepository.save(fine);
        }
    }

}