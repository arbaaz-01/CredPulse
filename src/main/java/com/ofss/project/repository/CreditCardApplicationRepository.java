package com.ofss.project.repository;

import com.ofss.project.entity.CreditCardApplication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardApplicationRepository
        extends JpaRepository<CreditCardApplication, Long> {

    List<CreditCardApplication>
    findByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<CreditCardApplication>
    findByIdAndUser_Id(Long id, Long userId);

    boolean existsByApplicationNumber(
            String applicationNumber
    );
}