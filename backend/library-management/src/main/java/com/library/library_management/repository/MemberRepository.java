package com.library.library_management.repository;

import com.library.library_management.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByMembershipNumber(String membershipNumber);
    Optional<Member> findByNationalCode(String nationalCode);
    boolean existsByNationalCode(String nationalCode);
    boolean existsByMembershipNumber(String membershipNumber);

}