package com.ofss.project.service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.HexFormat;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ofss.project.dto.response.CardIssuanceResponse;
import com.ofss.project.dto.response.CreditCardResponse;
import com.ofss.project.entity.CreditCard;
import com.ofss.project.entity.CreditCardApplication;
import com.ofss.project.enums.ApplicationStatus;
import com.ofss.project.enums.CardStatus;
import com.ofss.project.repository.CreditCardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final CardEncryptionService cardEncryptionService;

    private final SecureRandom secureRandom =
            new SecureRandom();


    // =========================================================
    // CARD ISSUANCE
    // =========================================================

    @Transactional
    public CardIssuanceResponse issueCard(
            CreditCardApplication application,
            BigDecimal approvedLimit
    ) {

        if (application.getStatus() != ApplicationStatus.APPROVED) {

            throw new IllegalStateException(
                    "Credit card can only be issued for an approved application"
            );
        }

        if (creditCardRepository.existsByApplicationId(
                application.getId())) {

            throw new IllegalStateException(
                    "Card already issued for this application"
            );
        }

        if (approvedLimit == null ||
                approvedLimit.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Approved credit limit must be greater than zero"
            );
        }

        // Generate card number
        String cardNumber =
                generateCardNumber();

        // Encrypt card number for database
        String encryptedCardNumber =
                cardEncryptionService.encrypt(cardNumber);

        // Hash card number for uniqueness/checking
        String cardNumberHash =
                hashCardNumber(cardNumber);

        // Store last four digits
        String lastFour =
                cardNumber.substring(
                        cardNumber.length() - 4
                );

        // Generate CVV
        String cvv =
                generateCvv();

        // Generate expiry
        LocalDate expiryDate =
                LocalDate.now().plusYears(5);

        CreditCard card =
                CreditCard.builder()
                        .user(application.getUser())
                        .application(application)
                        .cardNumberEncrypted(
                                encryptedCardNumber
                        )
                        .cardNumberHash(
                                cardNumberHash
                        )
                        .cardLastFour(
                                lastFour
                        )
                        .cardHolderName(
                                application
                                        .getUser()
                                        .getName()
                        )
                        .expiryMonth(
                                expiryDate.getMonthValue()
                        )
                        .expiryYear(
                                expiryDate.getYear()
                        )
                        .creditLimit(
                                approvedLimit
                        )
                        .availableLimit(
                                approvedLimit
                        )
                        .status(
                                CardStatus.ACTIVE
                        )
                        .build();

        CreditCard savedCard =
                creditCardRepository.save(card);

        return new CardIssuanceResponse(
                savedCard.getId(),
                maskCardNumber(cardNumber),
                cvv,
                savedCard.getExpiryMonth(),
                savedCard.getExpiryYear(),
                savedCard.getCreditLimit(),
                savedCard.getStatus().name()
        );
    }


    // =========================================================
    // GET ALL CARDS
    // =========================================================

   @Transactional(readOnly = true)
public List<CreditCardResponse> getUserCards(Long userId) {

    return creditCardRepository
            .findAllByUserId(userId)
            .stream()
            .map(this::toResponse)
            .toList();
}




    // =========================================================
    // GET ONE CARD
    // =========================================================

   @Transactional(readOnly = true)
public CreditCardResponse getUserCard(
        Long cardId,
        Long userId
) {

    CreditCard card =
            creditCardRepository
                    .findByIdAndUserId(cardId, userId)
                    .orElseThrow(() ->
                            new RuntimeException("Card not found")
                    );

    return toResponse(card);
}


    // =========================================================
    // CONVERT ENTITY -> RESPONSE
    // =========================================================

    private CreditCardResponse toResponse(
            CreditCard card
    ) {

        String decryptedCardNumber =
                cardEncryptionService.decrypt(
                        card.getCardNumberEncrypted()
                );

        return CreditCardResponse.from(
                card,
                decryptedCardNumber
        );
    }


    // =========================================================
    // CARD NUMBER GENERATION
    // =========================================================

    private String generateCardNumber() {

        StringBuilder number =
                new StringBuilder(16);

        for (int i = 0; i < 15; i++) {

            int digit =
                    secureRandom.nextInt(10);

            // First digit between 4 and 9
            if (i == 0) {
                digit =
                        4 + secureRandom.nextInt(6);
            }

            number.append(digit);
        }

        int checkDigit =
                calculateLuhnCheckDigit(
                        number.toString()
                );

        number.append(checkDigit);

        return number.toString();
    }


    private int calculateLuhnCheckDigit(
            String number
    ) {

        int sum = 0;

        boolean doubleDigit = true;

        for (int i = number.length() - 1;
             i >= 0;
             i--) {

            int digit =
                    number.charAt(i) - '0';

            if (doubleDigit) {

                digit *= 2;

                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;

            doubleDigit = !doubleDigit;
        }

        return (10 - (sum % 10)) % 10;
    }


    // =========================================================
    // CVV
    // =========================================================

    private String generateCvv() {

        int cvv =
                100 + secureRandom.nextInt(900);

        return String.valueOf(cvv);
    }


    // =========================================================
    // HASH CARD NUMBER
    // =========================================================

    private String hashCardNumber(
            String cardNumber
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            cardNumber.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat.of()
                    .formatHex(hash);

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "Unable to hash card number",
                    e
            );
        }
    }


    // =========================================================
    // MASK CARD NUMBER
    // =========================================================

    private String maskCardNumber(
            String cardNumber
    ) {

        return "**** **** **** "
                + cardNumber.substring(
                        cardNumber.length() - 4
                );
    }
}