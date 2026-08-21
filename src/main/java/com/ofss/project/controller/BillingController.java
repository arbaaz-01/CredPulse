package com.ofss.project.controller;

import com.ofss.project.dto.request.PaymentRequest;
import com.ofss.project.dto.response.CreditCardBillResponse;
import com.ofss.project.entity.CreditCardBill;
import com.ofss.project.service.BillingService;
import com.ofss.project.service.DpdService;
import com.ofss.project.service.NpaClassificationService;
import com.ofss.project.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillingController {

    private final PaymentService paymentService;
    private final BillingService billingService;
    private final DpdService dpdService;
    private final NpaClassificationService npaClassificationService;

    @PostMapping("/generate")
    public ResponseEntity<CreditCardBillResponse> generateBill(
            @RequestParam Long cardId,
            @RequestParam LocalDate billingDate,
            @RequestParam BigDecimal outstandingAmount) {

        CreditCardBillResponse bill = billingService.generateBill(
                cardId,
                billingDate,
                outstandingAmount);

        return ResponseEntity.ok(bill);
    }

    @PostMapping("/{billId}/payments")
    public ResponseEntity<CreditCardBillResponse> makePayment(
            @PathVariable Long billId,
            @Valid @RequestBody PaymentRequest request) {

        return ResponseEntity.ok(
                paymentService.makePayment(
                        billId,
                        request.amount()));
    }

    @PostMapping("/{billId}/dpd")
    public ResponseEntity<CreditCardBillResponse> updateDpd(
            @PathVariable Long billId,
            @RequestParam LocalDate evaluationDate) {

        CreditCardBill bill = dpdService.updateDpd(
                billId,
                evaluationDate);

        return ResponseEntity.ok(
                billingService.toResponseForService(bill));
    }

    @PostMapping("/{billId}/npa-classify")
public ResponseEntity<CreditCardBillResponse> classifyNpa(
        @PathVariable Long billId
) {

    CreditCardBill bill =
            npaClassificationService.classify(
                    billId
            );

    return ResponseEntity.ok(
            billingService.toResponseForService(bill)
    );
}
}