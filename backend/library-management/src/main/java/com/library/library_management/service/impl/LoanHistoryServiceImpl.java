package com.library.library_management.service.impl;

import com.library.library_management.config.LibraryProperties;
import com.library.library_management.dto.loan.LoanHistoryResponse;
import com.library.library_management.entity.Enums;
import com.library.library_management.entity.LoanTransaction;
import com.library.library_management.repository.LoanTransactionRepository;
import com.library.library_management.repository.specification.LoanSpecification;
import com.library.library_management.service.LoanHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoanHistoryServiceImpl implements LoanHistoryService {

    private final LoanTransactionRepository loanRepository;
    private final LibraryProperties properties;

    @Override
    public Page<LoanHistoryResponse> searchHistory(String membershipNumber, String bookCode, Enums.LoanType type, Enums.LoanStatus status, Enums.LoanState state, LocalDateTime from, LocalDateTime to, Pageable pageable) {

        Specification<LoanTransaction> specification = (root, query, cb) -> cb.conjunction();

        if (membershipNumber != null && !membershipNumber.isBlank()) {
            specification = specification.and(LoanSpecification.hasMember(membershipNumber));
        }

        if (bookCode != null && !bookCode.isBlank()) {
            specification = specification.and(LoanSpecification.hasBook(bookCode));
        }

        if (type != null) {
            specification = specification.and(LoanSpecification.hasType(type));
        }

        if (status != null) {
            specification = specification.and(LoanSpecification.hasStatus(status));
        }

        if (state != null) {
            specification = specification.and(LoanSpecification.hasState(state));
        }

        if (from != null) {
            specification = specification.and(LoanSpecification.dateFrom(from));
        }

        if (to != null) {
            specification = specification.and(LoanSpecification.dateTo(to));
        }

        return loanRepository.findAll(specification, pageable).map(this::mapToResponse);
    }

    private LoanHistoryResponse mapToResponse(LoanTransaction loan) {

        Long parentTransactionId = loan.getParentTransaction() != null ? loan.getParentTransaction().getId() : null;

        boolean hasChildTransaction = loanRepository.existsByParentTransactionId(loan.getId());

        boolean current = loan.getStatus() == Enums.LoanStatus.SUCCESS && loan.getType() != Enums.LoanType.RETURN && loan.getReturnDate() == null && !hasChildTransaction;

        boolean canReturn = current;

        boolean canRenew = current && loan.getRenewCount() != null && loan.getRenewCount() < properties.getMaxRenewCount();

        return new LoanHistoryResponse(loan.getId(), parentTransactionId, loan.getTrackingCode(), loan.getMember().getMembershipNumber(), loan.getBook().getBookCode(), loan.getType(), loan.getStatus(), loan.getRequestDate(), loan.getDueDate(), loan.getReturnDate(), loan.getRenewCount(), current, canReturn, canRenew);
    }
}