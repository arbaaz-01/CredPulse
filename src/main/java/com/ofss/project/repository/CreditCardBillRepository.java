package com.ofss.project.repository;

import com.ofss.project.entity.CreditCardBill;
import com.ofss.project.enums.BillStatus;
import com.ofss.project.enums.DpdBucket;
import com.ofss.project.enums.NpaClassification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CreditCardBillRepository
        extends JpaRepository<CreditCardBill, Long> {

    List<CreditCardBill> findByCreditCard_IdOrderByBillingDateDesc(
            Long cardId);

    Optional<CreditCardBill> findTopByCreditCard_IdOrderByBillingDateDesc(
            Long cardId);

    List<CreditCardBill> findByDueDateBeforeAndStatusIn(
            LocalDate date,
            List<BillStatus> statuses);

    List<CreditCardBill> findByDpdBucketOrderByDpdDesc(
            DpdBucket dpdBucket);

    List<CreditCardBill> findByDpdBucketInOrderByDpdDesc(
            List<DpdBucket> dpdBuckets);

    List<CreditCardBill> findByNpaClassificationOrderByDpdDesc(
            NpaClassification classification);

    boolean existsByCreditCard_IdAndBillingDate(
            Long cardId,
            LocalDate billingDate);
}