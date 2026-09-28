package com.library.library_management.repository;

import com.library.library_management.entity.MemberHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberHistoryRepository
        extends JpaRepository<MemberHistory, Long> {
    List<MemberHistory> findByMemberId(Long memberId);

}