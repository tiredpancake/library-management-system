package com.library.library_management.service.impl;

import com.library.library_management.dto.history.MemberHistoryResponse;
import com.library.library_management.entity.MemberHistory;
import com.library.library_management.repository.MemberHistoryRepository;
import com.library.library_management.security.SecurityUtils;
import com.library.library_management.service.EventLogger;
import com.library.library_management.service.MemberHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberHistoryServiceImpl implements MemberHistoryService {

    private final MemberHistoryRepository memberHistoryRepository;
    private final EventLogger eventLogger;

    @Override
    public List<MemberHistoryResponse> getMemberHistory(Long memberId) {
        List<MemberHistoryResponse> result = memberHistoryRepository.findByMemberId(memberId)
                .stream()
                .map(this::mapToResponse)
                .toList();

        eventLogger.info(
                "MEMBER_HISTORY_VIEW",
                SecurityUtils.getCurrentUsername(),
                "memberId=" + memberId + " count=" + result.size()
        );
        return result;
    }

    private MemberHistoryResponse mapToResponse(MemberHistory history) {
        return new MemberHistoryResponse(
                history.getId(),
                history.getFieldName(),
                history.getOldValue(),
                history.getNewValue(),
                history.getChangedBy().getUsername(),
                history.getChangedAt()
        );
    }
}
