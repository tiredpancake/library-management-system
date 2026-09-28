package com.library.library_management.service.impl;


import com.library.library_management.dto.history.MemberHistoryResponse;
import com.library.library_management.entity.MemberHistory;
import com.library.library_management.repository.MemberHistoryRepository;
import com.library.library_management.service.MemberHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class MemberHistoryServiceImpl
        implements MemberHistoryService {


    private final MemberHistoryRepository memberHistoryRepository;



    @Override
    public List<MemberHistoryResponse> getMemberHistory(Long memberId) {


        return memberHistoryRepository
                .findByMemberId(memberId)
                .stream()
                .map(this::mapToResponse)
                .toList();

    }



    private MemberHistoryResponse mapToResponse(
            MemberHistory history
    ) {

        return new MemberHistoryResponse(

                history.getId(),

                history.getFieldName(),

                history.getOldValue(),

                history.getNewValue(),

                history.getChangedBy()
                        .getUsername(),

                history.getChangedAt()

        );
    }

}