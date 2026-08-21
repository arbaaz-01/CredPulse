package com.ofss.project.service;

import com.ofss.project.dto.response.ManagerRiskSummaryResponse;
import com.ofss.project.entity.CreditCardBill;
import com.ofss.project.enums.DpdBucket;
import com.ofss.project.enums.NpaClassification;
import com.ofss.project.repository.CreditCardBillRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final CreditCardBillRepository billRepository;

    public ManagerRiskSummaryResponse getRiskSummary() {

        List<CreditCardBill> bills =
                billRepository.findAll();

        BigDecimal totalOutstanding =
                bills.stream()
                        .map(CreditCardBill::getRemainingAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal npaAmount =
                bills.stream()
                        .filter(bill ->
                                bill.getNpaClassification()
                                        == NpaClassification.NPA
                        )
                        .map(CreditCardBill::getRemainingAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal npaRatio =
                totalOutstanding.compareTo(BigDecimal.ZERO) == 0
                        ? BigDecimal.ZERO
                        : npaAmount
                                .divide(
                                        totalOutstanding,
                                        4,
                                        java.math.RoundingMode.HALF_UP
                                );

        return new ManagerRiskSummaryResponse(

                totalOutstanding,

                npaAmount,

                npaRatio,

                countByBucket(
                        bills,
                        DpdBucket.STANDARD
                ),
                amountByBucket(
                        bills,
                        DpdBucket.STANDARD
                ),

                countByBucket(
                        bills,
                        DpdBucket.SMA_0
                ),
                amountByBucket(
                        bills,
                        DpdBucket.SMA_0
                ),

                countByBucket(
                        bills,
                        DpdBucket.SMA_1
                ),
                amountByBucket(
                        bills,
                        DpdBucket.SMA_1
                ),

                countByBucket(
                        bills,
                        DpdBucket.SMA_2
                ),
                amountByBucket(
                        bills,
                        DpdBucket.SMA_2
                ),

                countByBucket(
                        bills,
                        DpdBucket.SUB_STANDARD
                ),
                amountByBucket(
                        bills,
                        DpdBucket.SUB_STANDARD
                ),

                countByBucket(
                        bills,
                        DpdBucket.DOUBTFUL
                ),
                amountByBucket(
                        bills,
                        DpdBucket.DOUBTFUL
                ),

                countByBucket(
                        bills,
                        DpdBucket.LOSS
                ),
                amountByBucket(
                        bills,
                        DpdBucket.LOSS
                )
        );
    }

    private long countByBucket(
            List<CreditCardBill> bills,
            DpdBucket bucket
    ) {

        return bills.stream()
                .filter(bill ->
                        bill.getDpdBucket() == bucket
                )
                .count();
    }

    private BigDecimal amountByBucket(
            List<CreditCardBill> bills,
            DpdBucket bucket
    ) {

        return bills.stream()
                .filter(bill ->
                        bill.getDpdBucket() == bucket
                )
                .map(CreditCardBill::getRemainingAmount)
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }
}