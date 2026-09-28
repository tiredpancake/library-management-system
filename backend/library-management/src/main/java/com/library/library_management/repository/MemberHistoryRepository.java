package com.library.library_management.repository;

import com.library.library_management.entity.MemberHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHistoryRepository
        extends JpaRepository<MemberHistory, Long> {

}