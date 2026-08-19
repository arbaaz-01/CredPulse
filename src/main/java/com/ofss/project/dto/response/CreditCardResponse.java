package com.ofss.project.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ofss.project.entity.CreditCard;

public record CreditCardResponse(

        Long id,

        String maskedCardNumber,
        String cardNumber,

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
            CreditCard card,
            String decryptedCardNumber
    ) {

        String maskedCardNumber =
                "**** **** **** "
                        + card.getCardLastFour();

        return new CreditCardResponse(

                card.getId(),

                maskedCardNumber,
                decryptedCardNumber,

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