package com.library.library_management.repository;

import com.library.library_management.entity.Member;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMembershipNumber(String membershipNumber);

    Optional<Member> findByNationalCode(String nationalCode);

    boolean existsByNationalCode(String nationalCode);

    boolean existsByMembershipNumber(String membershipNumber);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            select m
            from Member m
            where m.membershipNumber = :membershipNumber
            """)
    Optional<Member> findByMembershipNumberForUpdate(@Param("membershipNumber") String membershipNumber);
}