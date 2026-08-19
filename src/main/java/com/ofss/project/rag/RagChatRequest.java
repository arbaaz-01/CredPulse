package com.ofss.project.rag;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RagChatRequest(
        @NotBlank(message = "Question is required")
        @Size(max = 1000, message = "Question must not exceed 1000 characters")
        String question,
        @DecimalMin(value = "0.01", message = "Income must be greater than zero")
        BigDecimal income,
        @DecimalMin(value = "0.00", message = "Monthly expense cannot be negative")
        BigDecimal monthlyExpense,
        @DecimalMin(value = "0.00", message = "Travel expense cannot be negative")
        BigDecimal travelExpense,
        @DecimalMin(value = "0.00", message = "Shopping expense cannot be negative")
        BigDecimal shoppingExpense,
        @DecimalMin(value = "0.00", message = "Dining expense cannot be negative")
        BigDecimal diningExpense,
        @DecimalMin(value = "0.00", message = "Fuel expense cannot be negative")
        BigDecimal fuelExpense,
        @Size(max = 100, message = "Preferred benefit must not exceed 100 characters")
        String preferredBenefit) {
}
