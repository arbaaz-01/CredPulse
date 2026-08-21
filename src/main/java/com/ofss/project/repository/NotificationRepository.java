package com.ofss.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ofss.project.entity.Notification;
import com.ofss.project.enums.NotificationType;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	List<Notification> findByUser_IdOrderByCreatedAtDesc(Long userId);

	List<Notification> findByUser_IdAndReadStatusFalseOrderByCreatedAtDesc(Long userId);

	long countByUser_IdAndReadStatusFalse(Long userId);

	boolean existsByApplication_IdAndType(Long applicationId, NotificationType type);
}