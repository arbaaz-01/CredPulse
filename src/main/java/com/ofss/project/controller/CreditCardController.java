package com.ofss.project.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofss.project.dto.response.CreditCardResponse;
import com.ofss.project.service.CreditCardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/issued-cards")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class CreditCardController {

    private final CreditCardService creditCardService;

    @GetMapping
    public ResponseEntity<List<CreditCardResponse>> getMyCards(
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId =
                Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                creditCardService.getUserCards(userId)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<CreditCardResponse> getMyCard(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt
    ) {

        Long userId =
                Long.valueOf(jwt.getSubject());

        return ResponseEntity.ok(
                creditCardService.getUserCard(id, userId)
        );
    }
}