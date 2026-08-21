package com.ofss.project.service;

import com.ofss.project.dto.response.CollectionAccountResponse;
import com.ofss.project.entity.CreditCardBill;
import com.ofss.project.enums.DpdBucket;
import com.ofss.project.enums.NpaClassification;
import com.ofss.project.repository.CreditCardBillRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionsService {

    private final CreditCardBillRepository billRepository;

    public List<CollectionAccountResponse> getDelinquentBills() {

        return billRepository
                .findByDpdBucketInOrderByDpdDesc(
                        List.of(
                                DpdBucket.SMA_0,
                                DpdBucket.SMA_1,
                                DpdBucket.SMA_2,
                                DpdBucket.SUB_STANDARD,
                                DpdBucket.DOUBTFUL,
                                DpdBucket.LOSS
                        )
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<CollectionAccountResponse> getNpaBills() {

        return billRepository
                .findByNpaClassificationOrderByDpdDesc(
                        NpaClassification.NPA
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CollectionAccountResponse toResponse(
            CreditCardBill bill
    ) {

        return new CollectionAccountResponse(
                bill.getId(),
                bill.getCreditCard().getId(),
                bill.getCreditCard().getCardLastFour(),
                bill.getTotalOutstanding(),
                bill.getMinimumAmountDue(),
                bill.getPaidAmount(),
                bill.getRemainingAmount(),
                bill.getDpd(),
                bill.getDpdBucket(),
                bill.getNpaClassification(),
                bill.getStatus(),
                bill.getDueDate()
        );
    }
}