package com.ofss.project.dto.response;

import com.ofss.project.entity.CreditCard;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CreditCardResponse(

        Long id,
        String cardLastFour,
        String cardHolderName,
        Integer expiryMonth,
        Integer expiryYear,
        BigDecimal creditLimit,
        BigDecimal availableLimit,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CreditCardResponse from(
            CreditCard card) {

        return new CreditCardResponse(
                card.getId(),
                card.getCardLastFour(),
                card.getCardHolderName(),
                card.getExpiryMonth(),
                card.getExpiryYear(),
                card.getCreditLimit(),
                card.getAvailableLimit(),
                card.getStatus().name(),
                card.getCreatedAt(),
                card.getUpdatedAt()
        );
    }
}
