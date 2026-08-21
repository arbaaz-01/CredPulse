package com.ofss.project.dto.response;

import java.time.LocalDateTime;

import com.ofss.project.enums.CollectionActionType;

public record CollectionActionResponse(

        Long id,
        Long billId,
        CollectionActionType actionType,
        String remarks,
        LocalDateTime actionDate

) {
}