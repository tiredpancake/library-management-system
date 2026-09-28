package com.library.library_management.service.impl;

import com.library.library_management.dto.loan.*;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.exception.ResourceNotFoundException;
import com.library.library_management.repository.*;
import com.library.library_management.service.LoanService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.library.library_management.entity.*;
import com.library.library_management.entity.Enums;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {


    private final LoanTransactionRepository loanRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;
    private final AppUserRepository appUserRepository;
    private final FineRepository fineRepository;



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

        if (member.getStatus() != Enums.MemberStatus.ACTIVE) {

            throw new BusinessException(
                    "Member is not active"
            );
        }

        if (book.getAvailableCopies() <= 0) {

            throw new BusinessException(
                    "Book is not available"
            );
        }


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
                LocalDateTime.now().plusDays(14)
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
        LoanTransaction loan = loanRepository
                .findByTrackingCode(request.trackingCode())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Loan not found")
                );


        if (loan.getReturnDate() != null) {

            throw new BusinessException(
                    "Book already returned"
            );
        }

        loan.setReturnDate(
                LocalDateTime.now()
        );

        loan.setStatus(
                Enums.LoanStatus.SUCCESS
        );

        Book book = loan.getBook();

        book.setAvailableCopies(
                book.getAvailableCopies() + 1
        );

        bookRepository.save(book);


        LoanTransaction savedLoan =
                loanRepository.save(loan);
        createFineIfNeeded(savedLoan);



        return mapToResponse(savedLoan);    }



    @Override
    @Transactional
    public LoanResponse renewLoan(RenewRequest request) {
        LoanTransaction oldLoan = loanRepository
                .findByTrackingCode(request.trackingCode())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Loan not found")
                );


        if (oldLoan.getReturnDate() != null) {

            throw new BusinessException(
                    "Book already returned"
            );
        }


        LoanTransaction newLoan = new LoanTransaction();


        newLoan.setMember(
                oldLoan.getMember()
        );


        newLoan.setBook(
                oldLoan.getBook()
        );


        newLoan.setCreatedBy(
                getCurrentUser()
        );


        newLoan.setParentTransaction(
                oldLoan
        );


        newLoan.setType(
                Enums.LoanType.RENEW
        );


        newLoan.setStatus(
                Enums.LoanStatus.SUCCESS
        );


        newLoan.setTrackingCode(
                generateTrackingCode()
        );


        newLoan.setRequestDate(
                LocalDateTime.now()
        );


        newLoan.setDueDate(
                LocalDateTime.now()
                        .plusDays(14)
        );


        newLoan.setRenewCount(
                oldLoan.getRenewCount() + 1
        );


        LoanTransaction savedLoan =
                loanRepository.save(newLoan);


        return mapToResponse(savedLoan);    }

    private String generateTrackingCode() {

        return String.valueOf(
                System.currentTimeMillis()
        );

    }

    private AppUser getCurrentUser() {

        return appUserRepository
                .findByUsername("admin")
                .orElseThrow(
                        () -> new RuntimeException("Admin user not found")
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
                                    lateDays * 5000L,
                                    20000L
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