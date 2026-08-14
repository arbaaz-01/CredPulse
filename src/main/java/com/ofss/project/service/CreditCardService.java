package com.ofss.project.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ofss.project.dto.request.AddCardRequest;
import com.ofss.project.dto.request.UpdateCardStatusRequest;
import com.ofss.project.dto.response.CreditCardResponse;
import com.ofss.project.entity.CreditCard;
import com.ofss.project.entity.User;
import com.ofss.project.enums.CardStatus;
import com.ofss.project.exception.CardAlreadyExistsException;
import com.ofss.project.exception.CardNotFoundException;
import com.ofss.project.exception.InvalidCardNumberException;
import com.ofss.project.exception.InvalidCardStatusException;
import com.ofss.project.repository.CreditCardRepository;
import com.ofss.project.repository.UserRepository;
import com.ofss.project.security.CurrentUser;
import com.ofss.project.util.CardNumberUtil;
import com.ofss.project.util.HashUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final UserRepository userRepository;
    private final CurrentUser currentUser;
    private final HashUtil hashUtil;
    private final CardNumberUtil cardNumberUtil;


    @Transactional
    public CreditCardResponse addCard(
            Authentication authentication,
            AddCardRequest request) {

        Long userId =
                currentUser.getUserId(authentication);

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        String cardNumber = request.cardNumber().trim();
        
        if (!cardNumberUtil.isValid(cardNumber)) {
            throw new InvalidCardNumberException("Invalid card number");
        }

        String cardNumberHash =
                hashUtil.sha256(cardNumber);

        if (creditCardRepository
                .existsByUserIdAndCardNumberHash(
                        userId,
                        cardNumberHash
                )) {

            throw new CardAlreadyExistsException(
                    "This card is already added"
            );
        }

        String lastFour =
                cardNumber.substring(
                        cardNumber.length() - 4
                );

        BigDecimal creditLimit =
                request.creditLimit();

        CreditCard card = CreditCard.builder()
                .user(user)
                .cardNumberHash(cardNumberHash)
                .cardLastFour(lastFour)
                .cardHolderName(
                        request.cardHolderName()
                                .trim()
                                .toUpperCase()
                )
                .expiryMonth(request.expiryMonth())
                .expiryYear(request.expiryYear())
                .creditLimit(creditLimit)
                .availableLimit(creditLimit)
                .status(CardStatus.ACTIVE)
                .build();

        CreditCard saved =
                creditCardRepository.save(card);

        return CreditCardResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<CreditCardResponse> getMyCards(
            Authentication authentication) {

        Long userId =
                currentUser.getUserId(authentication);

        return creditCardRepository
                .findAllByUserIdAndStatusNot(
                        userId,
                        CardStatus.REMOVED
                )
                .stream()
                .map(CreditCardResponse::from)
                .toList();

    }

    @Transactional(readOnly = true)
    public CreditCardResponse getCard(
            Authentication authentication,
            Long cardId) {

        Long userId =
                currentUser.getUserId(authentication);

        CreditCard card =
                creditCardRepository
                        .findByIdAndUserId(
                                cardId,
                                userId
                        )
                        .orElseThrow(() ->
                                new CardNotFoundException(
                                        "Card not found"
                                )
                        );

        if (card.getStatus() == CardStatus.REMOVED) {
            throw new CardNotFoundException(
                    "Card not found"
            );
        }

        return CreditCardResponse.from(card);
    }

    
    @Transactional
    public CreditCardResponse updateCardStatus(
            Authentication authentication,
            Long cardId,
            UpdateCardStatusRequest request) {

        Long userId =
                currentUser.getUserId(authentication);

        CreditCard card =
                creditCardRepository
                        .findByIdAndUserId(
                                cardId,
                                userId
                        )
                        .orElseThrow(() ->
                                new CardNotFoundException(
                                        "Card not found"
                                )
                        );

        CardStatus newStatus = request.status();

        if (newStatus == CardStatus.REMOVED) {
            throw new InvalidCardStatusException(
                    "Use the remove card operation to remove a card"
            );
        }

        if (card.getStatus() == CardStatus.REMOVED) {
            throw new InvalidCardStatusException(
                    "Removed card cannot be modified"
            );
        }

        card.setStatus(newStatus);

        CreditCard updated =
                creditCardRepository.save(card);

        return CreditCardResponse.from(updated);
    }
    
    @Transactional
    public void removeCard(
            Authentication authentication,
            Long cardId) {

        Long userId =
                currentUser.getUserId(authentication);

        CreditCard card =
                creditCardRepository
                        .findByIdAndUserId(
                                cardId,
                                userId
                        )
                        .orElseThrow(() ->
                                new CardNotFoundException(
                                        "Card not found"
                                )
                        );

        if (card.getStatus() == CardStatus.REMOVED) {
            throw new CardNotFoundException(
                    "Card not found"
            );
        }

        card.setStatus(CardStatus.REMOVED);

        creditCardRepository.save(card);
    }


}
