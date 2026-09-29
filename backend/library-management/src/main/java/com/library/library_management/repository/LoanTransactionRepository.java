package com.library.library_management.repository;

import com.library.library_management.entity.LoanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanTransactionRepository
        extends JpaRepository<LoanTransaction, Long>,
        JpaSpecificationExecutor<LoanTransaction> {


    Optional<LoanTransaction> findByTrackingCode(String trackingCode);


    List<LoanTransaction> findByMemberId(Long memberId);


    long countByMemberIdAndReturnDateIsNull(Long memberId);


    boolean existsByMemberIdAndReturnDateIsNullAndDueDateBefore(
            Long memberId,
            LocalDateTime date
    );

    long countByReturnDateIsNull();

}