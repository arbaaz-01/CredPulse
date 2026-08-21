package com.ofss.project.service;

import com.ofss.project.dto.response.CreditCardBillResponse;
import com.ofss.project.entity.CreditCardBill;
import com.ofss.project.entity.CreditCardPayment;
import com.ofss.project.enums.BillStatus;
import com.ofss.project.enums.PaymentType;
import com.ofss.project.repository.CreditCardBillRepository;
import com.ofss.project.repository.CreditCardPaymentRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CreditCardBillRepository billRepository;
    private final CreditCardPaymentRepository paymentRepository;
    private final BillingService billingService;
    private final DpdService dpdService;

    @Transactional
    public CreditCardBillResponse makePayment(
            Long billId,
            BigDecimal amount) {

        CreditCardBill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException(
                        "Bill not found"));

        if (amount == null ||
                amount.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Payment amount must be greater than zero");
        }

        if (amount.compareTo(
                bill.getRemainingAmount()) > 0) {

            throw new IllegalArgumentException(
                    "Payment cannot exceed remaining bill amount");
        }

        /*
         * Determine payment type.
         */
        PaymentType paymentType;

        if (amount.compareTo(
                bill.getRemainingAmount()) == 0) {

            paymentType = PaymentType.FULL;

        } else if (amount.compareTo(
                bill.getMinimumAmountDue()) >= 0) {

            paymentType = PaymentType.MINIMUM;

        } else {

            paymentType = PaymentType.PARTIAL;
        }

        /*
         * Create payment record.
         */
        CreditCardPayment payment = CreditCardPayment.builder()
                .bill(bill)
                .paymentAmount(amount)
                .paymentDate(LocalDateTime.now())
                .paymentType(paymentType)
                .build();

        paymentRepository.save(payment);

        /*
         * Update bill totals.
         */
        BigDecimal newPaidAmount = bill.getPaidAmount()
                .add(amount);

        BigDecimal newRemainingAmount = bill.getRemainingAmount()
                .subtract(amount);

        bill.setPaidAmount(
                newPaidAmount);

        bill.setRemainingAmount(
                newRemainingAmount);

        /*
         * Determine current payment state.
         */
        if (newRemainingAmount.compareTo(
                BigDecimal.ZERO) == 0) {

            bill.setStatus(
                    BillStatus.PAID);

        } else if (newPaidAmount.compareTo(
                bill.getMinimumAmountDue()) >= 0) {

            bill.setStatus(
                    BillStatus.MAD_PAID);

        } else {

            bill.setStatus(
                    BillStatus.PARTIALLY_PAID);
        }

        CreditCardBill savedBill = billRepository.save(bill);

        CreditCardBill updatedBill = dpdService.recalculateAfterPayment(
                savedBill,
                LocalDate.now());

        return billingService.toResponseForService(
                updatedBill);
    }
}