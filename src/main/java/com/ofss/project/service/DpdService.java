package com.ofss.project.service;

import com.ofss.project.entity.CreditCardBill;
import com.ofss.project.enums.BillStatus;
import com.ofss.project.enums.DpdBucket;
import com.ofss.project.enums.NpaClassification;
import com.ofss.project.repository.CreditCardBillRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class DpdService {

    private final CreditCardBillRepository billRepository;

    @Transactional
    public CreditCardBill updateDpd(
            Long billId,
            LocalDate evaluationDate) {

        CreditCardBill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException(
                        "Bill not found"));

        /*
         * If the bill has been completely paid,
         * there is no overdue balance.
         */
        if (bill.getRemainingAmount()
                .compareTo(java.math.BigDecimal.ZERO) == 0) {

            bill.setDpd(0);
            bill.setDpdBucket(DpdBucket.STANDARD);
            bill.setNpaClassification(
                    NpaClassification.PERFORMING);

            return billRepository.save(bill);
        }

        /*
         * Payment has satisfied MAD.
         * For now, keep the account performing.
         */
        if (bill.getPaidAmount()
                .compareTo(
                        bill.getMinimumAmountDue()) >= 0) {

            bill.setDpd(0);
            bill.setDpdBucket(DpdBucket.STANDARD);
            bill.setNpaClassification(
                    NpaClassification.PERFORMING);

            return billRepository.save(bill);
        }

        /*
         * Due date has not passed yet.
         */
        if (evaluationDate.isBefore(
                bill.getDueDate())) {

            bill.setDpd(0);
            bill.setDpdBucket(DpdBucket.STANDARD);
            bill.setNpaClassification(
                    NpaClassification.PERFORMING);

            return billRepository.save(bill);
        }

        /*
         * MAD is unpaid after due date.
         */
        long daysPastDue = ChronoUnit.DAYS.between(
                bill.getDueDate(),
                evaluationDate);

        int dpd = (int) Math.max(
                0,
                daysPastDue);

        DpdBucket bucket = determineBucket(dpd);

        bill.setDpd(dpd);

        bill.setDpdBucket(bucket);

        bill.setNpaClassification(
                determineNpaClassification(bucket));

        bill.setStatus(
                BillStatus.OVERDUE);

        return billRepository.save(bill);
    }

    private DpdBucket determineBucket(int dpd) {

        if (dpd == 0) {
            return DpdBucket.STANDARD;
        }

        if (dpd <= 30) {
            return DpdBucket.SMA_0;
        }

        if (dpd <= 60) {
            return DpdBucket.SMA_1;
        }

        if (dpd <= 90) {
            return DpdBucket.SMA_2;
        }

        if (dpd <= 365) {
            return DpdBucket.SUB_STANDARD;
        }

        return DpdBucket.DOUBTFUL;
    }

    @Transactional
    public CreditCardBill recalculateAfterPayment(
            CreditCardBill bill,
            LocalDate evaluationDate) {

        /*
         * If the customer has now satisfied the MAD,
         * cure the delinquency.
         */
        if (bill.getPaidAmount()
                .compareTo(bill.getMinimumAmountDue()) >= 0) {

            bill.setDpd(0);
            bill.setDpdBucket(DpdBucket.STANDARD);

            bill.setNpaClassification(
                    NpaClassification.PERFORMING);

            /*
             * Full payment
             */
            if (bill.getRemainingAmount()
                    .compareTo(java.math.BigDecimal.ZERO) == 0) {

                bill.setStatus(BillStatus.PAID);

            } else {

                bill.setStatus(BillStatus.MAD_PAID);
            }

            return billRepository.save(bill);
        }

        /*
         * MAD is still not satisfied.
         * Recalculate DPD normally.
         */
        return updateDpd(
                bill.getId(),
                evaluationDate);
    }

    private NpaClassification determineNpaClassification(
            DpdBucket bucket) {

        switch (bucket) {

            case STANDARD:
            case SMA_0:
            case SMA_1:
            case SMA_2:
                return NpaClassification.PERFORMING;

            case SUB_STANDARD:
            case DOUBTFUL:
            case LOSS:
                return NpaClassification.NPA;

            default:
                throw new IllegalStateException(
                        "Unknown DPD bucket: " + bucket);
        }
    }
}