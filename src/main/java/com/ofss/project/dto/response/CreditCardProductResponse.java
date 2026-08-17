package com.ofss.project.dto.response;

import com.ofss.project.enums.CreditCardProductStatus;

import java.math.BigDecimal;

public record CreditCardProductResponse(

        Long id,
        String productCode,
        String name,
        String description,
        BigDecimal minimumIncome,
        BigDecimal annualFee,
        BigDecimal interestRate,
        BigDecimal minCreditLimit,
        BigDecimal maxCreditLimit,
        CreditCardProductStatus status
) {
}