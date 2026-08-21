package com.ofss.project.dto.response;

import com.ofss.project.enums.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(

        Long id,

        Long applicationId,

        String applicationNumber,

        NotificationType type,

        String title,

        String message,

        boolean read,

        LocalDateTime createdAt,

        LocalDateTime readAt
) {
}