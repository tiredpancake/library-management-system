package com.library.library_management.dto.history;

import java.time.LocalDateTime;

public record MemberHistoryResponse(

        Long id,

        String fieldName,

        String oldValue,

        String newValue,

        String changedBy,

        LocalDateTime changedAt

) {
}