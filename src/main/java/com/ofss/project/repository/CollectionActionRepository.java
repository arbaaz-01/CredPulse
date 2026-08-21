package com.ofss.project.repository;

import com.ofss.project.entity.CollectionAction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CollectionActionRepository
        extends JpaRepository<CollectionAction, Long> {

    List<CollectionAction> findByBill_IdOrderByActionDateAsc(
            Long billId
    );
}