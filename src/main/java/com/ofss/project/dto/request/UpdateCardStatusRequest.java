package com.ofss.project.dto.request;

import com.ofss.project.enums.CardStatus;

import jakarta.validation.constraints.NotNull;

public record UpdateCardStatusRequest(

        @NotNull(message = "Status is required")
        CardStatus status
) {
}
