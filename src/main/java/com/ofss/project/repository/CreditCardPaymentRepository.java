package com.ofss.project.repository;

import com.ofss.project.entity.CreditCardPayment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CreditCardPaymentRepository
        extends JpaRepository<CreditCardPayment, Long> {

    List<CreditCardPayment>
    findByBill_IdOrderByPaymentDateAsc(
            Long billId
    );
}