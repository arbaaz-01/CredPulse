package com.ofss.project.service;

import com.ofss.project.entity.CreditCardBill;
import com.ofss.project.enums.DpdBucket;
import com.ofss.project.enums.NpaClassification;
import com.ofss.project.repository.CreditCardBillRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NpaClassificationService {

    private final CreditCardBillRepository billRepository;

    @Transactional
    public CreditCardBill classify(Long billId) {

        CreditCardBill bill =
                billRepository.findById(billId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Bill not found"
                                )
                        );

        NpaClassification classification;

        DpdBucket bucket =
                bill.getDpdBucket();

        switch (bucket) {

            case STANDARD:
            case SMA_0:
            case SMA_1:
            case SMA_2:
                classification =
                        NpaClassification.PERFORMING;
                break;

            case SUB_STANDARD:
            case DOUBTFUL:
            case LOSS:
                classification =
                        NpaClassification.NPA;
                break;

            default:
                throw new IllegalStateException(
                        "Unknown DPD bucket: " + bucket
                );
        }

        bill.setNpaClassification(
                classification
        );

        return billRepository.save(bill);
    }
}