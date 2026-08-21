package com.ofss.project.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.ofss.project.enums.BillStatus;
import com.ofss.project.enums.DpdBucket;
import com.ofss.project.enums.NpaClassification;

public record CollectionAccountResponse(
        Long billId,
        Long cardId,
        String cardLastFour,
        BigDecimal outstandingAmount,
        BigDecimal minimumAmountDue,
        BigDecimal paidAmount,
        BigDecimal remainingAmount,
        Integer dpd,
        DpdBucket dpdBucket,
        NpaClassification npaClassification,
        BillStatus status,
        LocalDate dueDate
) {
}