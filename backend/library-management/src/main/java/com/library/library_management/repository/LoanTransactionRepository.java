package com.library.library_management.repository;

import com.library.library_management.entity.Enums;
import com.library.library_management.entity.LoanTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoanTransactionRepository extends JpaRepository<LoanTransaction, Long>, JpaSpecificationExecutor<LoanTransaction> {

    Optional<LoanTransaction> findByTrackingCode(String trackingCode);

    List<LoanTransaction> findByMemberId(Long memberId);

    Optional<LoanTransaction> findFirstByMember_IdAndBook_IdOrderByIdDesc(Long memberId, Long bookId);

    @Query("""
            SELECT CASE
                WHEN COUNT(l) > 0 THEN true
                ELSE false
            END
            FROM LoanTransaction l
            WHERE l.parentTransaction.id = :parentTransactionId
            """)
    boolean existsByParentTransactionId(@Param("parentTransactionId") Long parentTransactionId);

    @Query("""
            SELECT CASE
                WHEN COUNT(l) > 0 THEN true
                ELSE false
            END
            FROM LoanTransaction l
            WHERE l.member.id = :memberId
              AND l.book.id = :bookId
              AND l.status = :status
              AND l.type IN :activeTypes
              AND NOT EXISTS (
                  SELECT child.id
                  FROM LoanTransaction child
                  WHERE child.parentTransaction = l
              )
            """)
    boolean existsCurrentActiveLoan(@Param("memberId") Long memberId, @Param("bookId") Long bookId, @Param("status") Enums.LoanStatus status, @Param("activeTypes") List<Enums.LoanType> activeTypes);

    @Query("""
            SELECT COUNT(l)
            FROM LoanTransaction l
            WHERE l.member.id = :memberId
              AND l.status = :status
              AND l.type IN :activeTypes
              AND NOT EXISTS (
                  SELECT child.id
                  FROM LoanTransaction child
                  WHERE child.parentTransaction = l
              )
            """)
    long countCurrentActiveLoans(@Param("memberId") Long memberId, @Param("status") Enums.LoanStatus status, @Param("activeTypes") List<Enums.LoanType> activeTypes);

    /*
     * Compatibility method used by LoanValidator.
     *
     * Counts only the member's CURRENT active loans.
     * BORROW/RENEW are considered active transaction types.
     *
     * If a transaction has a child transaction (for example RENEW
     * or RETURN), that old transaction is not counted as current.
     */
    default long countCurrentActiveLoansByMemberId(Long memberId) {

        return countCurrentActiveLoans(memberId, Enums.LoanStatus.SUCCESS, List.of(Enums.LoanType.BORROW, Enums.LoanType.RENEW));
    }

    @Query("""
            SELECT CASE
                WHEN COUNT(l) > 0 THEN true
                ELSE false
            END
            FROM LoanTransaction l
            WHERE l.member.id = :memberId
              AND l.status = :status
              AND l.type IN :activeTypes
              AND l.dueDate < :date
              AND NOT EXISTS (
                  SELECT child.id
                  FROM LoanTransaction child
                  WHERE child.parentTransaction = l
              )
            """)
    boolean existsCurrentOverdueLoan(@Param("memberId") Long memberId, @Param("status") Enums.LoanStatus status, @Param("activeTypes") List<Enums.LoanType> activeTypes, @Param("date") LocalDateTime date);

    /*
     * Counts CURRENT overdue loans for one member.
     *
     * This is separate from existsCurrentOverdueLoan because
     * LoanValidator needs the actual number.
     */
    @Query("""
            SELECT COUNT(l)
            FROM LoanTransaction l
            WHERE l.member.id = :memberId
              AND l.status = :status
              AND l.type IN :activeTypes
              AND l.dueDate < :date
              AND NOT EXISTS (
                  SELECT child.id
                  FROM LoanTransaction child
                  WHERE child.parentTransaction = l
              )
            """)
    long countCurrentOverdueLoans(@Param("memberId") Long memberId, @Param("status") Enums.LoanStatus status, @Param("activeTypes") List<Enums.LoanType> activeTypes, @Param("date") LocalDateTime date);

    /*
     * Compatibility method used by LoanValidator.
     */
    default long countCurrentOverdueLoansByMemberId(Long memberId, LocalDateTime date) {

        return countCurrentOverdueLoans(memberId, Enums.LoanStatus.SUCCESS, List.of(Enums.LoanType.BORROW, Enums.LoanType.RENEW), date);
    }

    @Query("""
            SELECT COUNT(l)
            FROM LoanTransaction l
            WHERE l.status = :status
              AND l.type IN :activeTypes
              AND NOT EXISTS (
                  SELECT child.id
                  FROM LoanTransaction child
                  WHERE child.parentTransaction = l
              )
            """)
    long countCurrentActiveLoans(@Param("status") Enums.LoanStatus status, @Param("activeTypes") List<Enums.LoanType> activeTypes);

    /*
     * Compatibility method used by DashboardController.
     *
     * Dashboard asks for the total number of current active loans
     * without supplying filters.
     */
    default long countCurrentActiveLoans() {

        return countCurrentActiveLoans(Enums.LoanStatus.SUCCESS, List.of(Enums.LoanType.BORROW, Enums.LoanType.RENEW));
    }

    /*
     * Older/simple counting methods kept unchanged.
     */
    long countByMemberIdAndReturnDateIsNull(Long memberId);

    boolean existsByMemberIdAndReturnDateIsNullAndDueDateBefore(Long memberId, LocalDateTime date);

    long countByReturnDateIsNull();
}
