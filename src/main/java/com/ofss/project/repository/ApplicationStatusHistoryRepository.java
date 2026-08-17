package com.ofss.project.repository;

import com.ofss.project.entity.ApplicationStatusHistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationStatusHistoryRepository
        extends JpaRepository<ApplicationStatusHistory, Long> {

    List<ApplicationStatusHistory>
    findByApplication_IdOrderByChangedAtAsc(
            Long applicationId
    );
}