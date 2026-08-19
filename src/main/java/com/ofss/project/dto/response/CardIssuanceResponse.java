package com.ofss.project.dto.response;

import java.math.BigDecimal;

public record CardIssuanceResponse(
        Long cardId,
        String maskedCardNumber,
        String cvv,
        Integer expiryMonth,
        Integer expiryYear,
        BigDecimal creditLimit,
        String status
) {
}