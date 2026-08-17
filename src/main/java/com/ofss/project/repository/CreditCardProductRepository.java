package com.ofss.project.repository;

import com.ofss.project.entity.CreditCardProduct;
import com.ofss.project.enums.CreditCardProductStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CreditCardProductRepository
        extends JpaRepository<CreditCardProduct, Long> {

    List<CreditCardProduct> findByStatusOrderByNameAsc(
            CreditCardProductStatus status
    );

    Optional<CreditCardProduct> findByIdAndStatus(
            Long id,
            CreditCardProductStatus status
    );
}