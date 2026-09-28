package com.library.library_management.repository;

import com.library.library_management.entity.LoanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long> {

    Optional<LoanTransaction> findByTrackingCode(String trackingCode);
    List<LoanTransaction> findByMemberId(Long memberId);

}