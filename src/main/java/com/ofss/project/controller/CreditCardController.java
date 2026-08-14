package com.ofss.project.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofss.project.dto.request.AddCardRequest;
import com.ofss.project.dto.request.UpdateCardStatusRequest;
import com.ofss.project.dto.response.CreditCardResponse;
import com.ofss.project.service.CreditCardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService creditCardService;

    @PostMapping
    public ResponseEntity<CreditCardResponse> addCard(
            Authentication authentication,
            @Valid @RequestBody AddCardRequest request) {

        CreditCardResponse response =
                creditCardService.addCard(
                        authentication,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<CreditCardResponse>>
    getMyCards(
            Authentication authentication) {

        return ResponseEntity.ok(
                creditCardService.getMyCards(
                        authentication
                )
        );
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<CreditCardResponse> getCard(
            Authentication authentication,
            @PathVariable Long cardId) {

        return ResponseEntity.ok(
                creditCardService.getCard(
                        authentication,
                        cardId
                )
        );
    }
    
    @PatchMapping("/{cardId}/status")
    public ResponseEntity<CreditCardResponse> updateCardStatus(
            Authentication authentication,
            @PathVariable Long cardId,
            @Valid @RequestBody UpdateCardStatusRequest request) {

        return ResponseEntity.ok(
                creditCardService.updateCardStatus(
                        authentication,
                        cardId,
                        request
                )
        );
    }
    
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> removeCard(
            Authentication authentication,
            @PathVariable Long cardId) {

        creditCardService.removeCard(
                authentication,
                cardId
        );

        return ResponseEntity.noContent().build();
    }


}
