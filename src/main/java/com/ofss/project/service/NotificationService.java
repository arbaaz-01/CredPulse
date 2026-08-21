package com.ofss.project.service;

import com.ofss.project.dto.response.NotificationResponse;
import com.ofss.project.entity.CreditCardApplication;
import com.ofss.project.entity.Notification;
import com.ofss.project.entity.User;
import com.ofss.project.enums.NotificationType;
import com.ofss.project.exception.NotificationNotFoundException;
import com.ofss.project.repository.NotificationRepository;
import com.ofss.project.security.CurrentUser;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final CurrentUser currentUser;

    @Transactional
    public void createApplicationSubmittedNotification(
            CreditCardApplication application) {

        createNotification(
                application,
                NotificationType.APPLICATION_SUBMITTED,
                "Application Submitted",
                "Your credit card application "
                        + application.getApplicationNumber()
                        + " has been submitted successfully."
        );
    }

    @Transactional
    public void createApplicationApprovedNotification(
            CreditCardApplication application) {

        createNotification(
                application,
                NotificationType.APPLICATION_APPROVED,
                "Application Approved",
                "Your credit card application "
                        + application.getApplicationNumber()
                        + " has been approved."
        );
    }

    @Transactional
    public void createApplicationRejectedNotification(
            CreditCardApplication application,
            String reason) {

        String message =
                "Your credit card application "
                        + application.getApplicationNumber()
                        + " has been rejected.";

        if (reason != null && !reason.isBlank()) {
            message += " Reason: " + reason.trim();
        }

        createNotification(
                application,
                NotificationType.APPLICATION_REJECTED,
                "Application Rejected",
                message
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications() {

        Long userId = currentUser.getUserId();

        return notificationRepository
                .findByUser_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyUnreadNotifications() {

        Long userId = currentUser.getUserId();

        return notificationRepository
                .findByUser_IdAndReadStatusFalseOrderByCreatedAtDesc(
                        userId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount() {

        return notificationRepository
                .countByUser_IdAndReadStatusFalse(
                        currentUser.getUserId()
                );
    }

    @Transactional
    public void markAsRead(Long notificationId) {

        Long userId = currentUser.getUserId();

        Notification notification =
                notificationRepository
                        .findById(notificationId)
                        .orElseThrow(() ->
                                new NotificationNotFoundException(
                                        "Notification not found"
                                )
                        );

        if (!notification
                .getUser()
                .getId()
                .equals(userId)) {

            throw new NotificationNotFoundException(
                    "Notification not found"
            );
        }

        if (!notification.isReadStatus()) {

            notification.setReadStatus(true);
            notification.setReadAt(
                    LocalDateTime.now()
            );

            notificationRepository.save(notification);
        }
    }

    private void createNotification(
            CreditCardApplication application,
            NotificationType type,
            String title,
            String message) {

        User user = application.getUser();

        Notification notification =
                Notification.builder()
                        .user(user)
                        .application(application)
                        .type(type)
                        .title(title)
                        .message(message)
                        .readStatus(false)
                        .build();

        notificationRepository.save(notification);
    }

    private NotificationResponse toResponse(
            Notification notification) {

        CreditCardApplication application =
                notification.getApplication();

        return new NotificationResponse(
                notification.getId(),
                application != null
                        ? application.getId()
                        : null,
                application != null
                        ? application.getApplicationNumber()
                        : null,
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.isReadStatus(),
                notification.getCreatedAt(),
                notification.getReadAt()
        );
    }
}