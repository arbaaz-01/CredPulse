package com.ofss.project.dto.request;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddCardRequest(

        @NotBlank(message = "Card number is required")
        @Pattern(
            regexp = "\\d{13,19}",
            message = "Card number must contain 13 to 19 digits"
        )
        String cardNumber,

        @NotBlank(message = "Card holder name is required")
        @Size(
            max = 100,
            message = "Card holder name cannot exceed 100 characters"
        )
        String cardHolderName,

        @NotNull(message = "Expiry month is required")
        @Min(value = 1, message = "Expiry month must be between 1 and 12")
        @Max(value = 12, message = "Expiry month must be between 1 and 12")
        Integer expiryMonth,

        @NotNull(message = "Expiry year is required")
        @Min(value = 2026, message = "Invalid expiry year")
        Integer expiryYear,

        @NotNull(message = "Credit limit is required")
        @DecimalMin(
            value = "0.01",
            message = "Credit limit must be greater than zero"
        )
        BigDecimal creditLimit
) {
}
