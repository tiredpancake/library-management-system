package com.library.library_management.service.validation;

import com.library.library_management.config.LibraryProperties;
import com.library.library_management.entity.Book;
import com.library.library_management.entity.Enums;
import com.library.library_management.entity.Member;
import com.library.library_management.exception.BusinessException;
import com.library.library_management.repository.FineRepository;
import com.library.library_management.repository.LoanTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class LoanValidator {

    private final LoanTransactionRepository loanRepository;
    private final LibraryProperties properties;
    private final FineRepository fineRepository;


    public void validateMember(Member member) {

        if (member.getStatus() != Enums.MemberStatus.ACTIVE) {
            throw new BusinessException("Member is not active");
        }
    }


    public void validateBook(Book book) {

        if (book.getStatus() != Enums.BookStatus.ACTIVE) {
            throw new BusinessException("Book is not active");
        }


        if (book.getAvailableCopies() <= 0) {
            throw new BusinessException("Book is not available");
        }
    }

    public void validateLoanLimit(Member member) {

        long activeLoans = loanRepository.countByMemberIdAndReturnDateIsNull(member.getId());


        if (activeLoans >= properties.getMaxActiveLoans()) {

            throw new BusinessException("Member reached maximum active loans");
        }
    }

    public void validateOverdue(Member member) {

        boolean hasOverdue = loanRepository.existsByMemberIdAndReturnDateIsNullAndDueDateBefore(member.getId(), LocalDateTime.now());

        if (hasOverdue) {

            throw new BusinessException("Member has overdue books");
        }
    }

    public void validateUnpaidFine(Member member) {

        BigDecimal unpaid = fineRepository.sumUnpaidFineByMemberId(member.getId());


        if (unpaid.compareTo(BigDecimal.valueOf(properties.getMaxUnpaidFine())) > 0) {

            throw new BusinessException("Member has too much unpaid fine");
        }
    }
}
