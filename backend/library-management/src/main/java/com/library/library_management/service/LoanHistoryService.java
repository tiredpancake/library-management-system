package com.library.library_management.service;


import com.library.library_management.dto.loan.LoanHistoryResponse;
import com.library.library_management.entity.Enums;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;


public interface LoanHistoryService {


    Page<LoanHistoryResponse> searchHistory(
            String membershipNumber,
            String bookCode,
            Enums.LoanType type,
            Enums.LoanStatus status,
            Enums.LoanState state,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

}