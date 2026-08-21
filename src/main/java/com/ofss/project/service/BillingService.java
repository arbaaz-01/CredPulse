package com.ofss.project.service;

import com.ofss.project.dto.response.CreditCardBillResponse;
import com.ofss.project.entity.CreditCard;
import com.ofss.project.entity.CreditCardBill;
import com.ofss.project.enums.BillStatus;
import com.ofss.project.enums.DpdBucket;
import com.ofss.project.enums.NpaClassification;
import com.ofss.project.repository.CreditCardBillRepository;
import com.ofss.project.repository.CreditCardRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final CreditCardRepository creditCardRepository;
    private final CreditCardBillRepository creditCardBillRepository;

    @Transactional
    public CreditCardBillResponse generateBill(
            Long cardId,
            LocalDate billingDate,
            BigDecimal outstandingAmount) {

        CreditCard card = creditCardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException(
                        "Credit card not found"));

        // Prevent duplicate bill for the same card/month
        if (creditCardBillRepository
                .existsByCreditCard_IdAndBillingDate(
                        cardId,
                        billingDate)) {

            throw new IllegalStateException(
                    "Bill already exists for this billing date");
        }

        if (outstandingAmount == null ||
                outstandingAmount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Outstanding amount must be greater than zero");
        }

        // Demo rule for MAD:
        // 10% of outstanding amount
        BigDecimal minimumAmountDue = outstandingAmount
                .multiply(new BigDecimal("0.10"));

        // Current version: no interest/late fee yet
        BigDecimal interestAmount = BigDecimal.ZERO;

        BigDecimal lateFee = BigDecimal.ZERO;

        BigDecimal dueAmount = outstandingAmount
                .add(interestAmount)
                .add(lateFee);

        LocalDate dueDate = billingDate.plusDays(25);

        CreditCardBill bill = CreditCardBill.builder()
                .creditCard(card)
                .billingDate(billingDate)
                .dueDate(dueDate)
                .totalOutstanding(dueAmount)
                .minimumAmountDue(minimumAmountDue)
                .interestAmount(interestAmount)
                .lateFee(lateFee)
                .paidAmount(BigDecimal.ZERO)
                .remainingAmount(dueAmount)
                .dpd(0)
                .dpdBucket(DpdBucket.STANDARD)
                .npaClassification(NpaClassification.PERFORMING)
                .status(BillStatus.UNPAID)
                .build();

        CreditCardBill savedBill = creditCardBillRepository.save(bill);

        return toResponse(savedBill);
    }

    CreditCardBillResponse toResponse(
            CreditCardBill bill) {

        return new CreditCardBillResponse(
                bill.getId(),
                bill.getCreditCard().getId(),
                bill.getCreditCard().getCardLastFour(),
                bill.getBillingDate(),
                bill.getDueDate(),
                bill.getTotalOutstanding(),
                bill.getMinimumAmountDue(),
                bill.getInterestAmount(),
                bill.getLateFee(),
                bill.getPaidAmount(),
                bill.getRemainingAmount(),
                bill.getDpd(),
                bill.getDpdBucket(),
                bill.getNpaClassification(),
                bill.getStatus(),
                bill.getCreatedAt(),
                bill.getUpdatedAt());
    }

    public CreditCardBillResponse toResponseForService(
            CreditCardBill bill) {
        return toResponse(bill);
    }
}