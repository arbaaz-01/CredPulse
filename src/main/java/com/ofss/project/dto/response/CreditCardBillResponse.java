package com.ofss.project.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.ofss.project.enums.BillStatus;
import com.ofss.project.enums.DpdBucket;
import com.ofss.project.enums.NpaClassification;

public record CreditCardBillResponse(
        Long id,
        Long cardId,
        String cardLastFour,
        LocalDate billingDate,
        LocalDate dueDate,
        BigDecimal totalOutstanding,
        BigDecimal minimumAmountDue,
        BigDecimal interestAmount,
        BigDecimal lateFee,
        BigDecimal paidAmount,
        BigDecimal remainingAmount,
        Integer dpd,
        DpdBucket dpdBucket,
        NpaClassification npaClassification,
        BillStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}