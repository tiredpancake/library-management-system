package com.library.library_management.service.impl;


import com.library.library_management.dto.loan.LoanHistoryResponse;
import com.library.library_management.entity.LoanTransaction;
import com.library.library_management.entity.Enums;
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
public class LoanHistoryServiceImpl
        implements LoanHistoryService {


    private final LoanTransactionRepository loanRepository;



    @Override
    public Page<LoanHistoryResponse> searchHistory(

            String membershipNumber,

            String bookCode,

            Enums.LoanType type,

            Enums.LoanStatus status,

            LocalDateTime from,

            LocalDateTime to,

            Pageable pageable

    ) {


        Specification<LoanTransaction> specification =
                Specification
                        .where(
                                LoanSpecification.hasMember(
                                        membershipNumber
                                )
                        )
                        .and(
                                LoanSpecification.hasBook(
                                        bookCode
                                )
                        )
                        .and(
                                LoanSpecification.hasType(
                                        type
                                )
                        )
                        .and(
                                LoanSpecification.hasStatus(
                                        status
                                )
                        )
                        .and(
                                LoanSpecification.dateFrom(
                                        from
                                )
                        )
                        .and(
                                LoanSpecification.dateTo(
                                        to
                                )
                        );



        return loanRepository
                .findAll(
                        specification,
                        pageable
                )
                .map(this::mapToResponse);

    }



    private LoanHistoryResponse mapToResponse(
            LoanTransaction loan
    ) {


        return new LoanHistoryResponse(

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

                loan.getReturnDate()

        );

    }

}