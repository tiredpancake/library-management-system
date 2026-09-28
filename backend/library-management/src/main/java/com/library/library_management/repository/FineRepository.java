package com.library.library_management.repository;

import com.library.library_management.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FineRepository extends JpaRepository<Fine, Long> {

    Optional<Fine> findByLoanTransactionId(Long loanTransactionId);

}