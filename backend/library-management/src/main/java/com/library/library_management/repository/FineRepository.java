package com.library.library_management.repository;

import com.library.library_management.entity.Enums;
import com.library.library_management.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByLoanTransactionId(Long loanTransactionId);


    @Query("""
       SELECT COALESCE(SUM(f.amount - f.paidAmount), 0)
       FROM Fine f
       WHERE f.loanTransaction.member.id = :memberId
       AND f.status <> com.library.library_management.entity.Enums.FineStatus.PAID
       """)
    BigDecimal sumUnpaidFineByMemberId(Long memberId);


    List<Fine> findByLoanTransactionMemberIdAndStatusNot(
            Long memberId,
            Enums.FineStatus status
    );


    long countByStatus(Enums.FineStatus status);
}