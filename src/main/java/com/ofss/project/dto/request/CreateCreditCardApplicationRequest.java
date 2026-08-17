package com.ofss.project.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateCreditCardApplicationRequest(

        @NotNull(message = "Product ID is required")
        Long productId,

        @NotNull(message = "Requested credit limit is required")
        @DecimalMin(
                value = "1.00",
                message = "Requested credit limit must be greater than zero"
        )
        BigDecimal requestedCreditLimit
) {
}