package com.library.library_management.service;

import com.library.library_management.dto.history.MemberHistoryResponse;

import java.util.List;

public interface MemberHistoryService {


    List<MemberHistoryResponse> getMemberHistory(Long memberId);

}