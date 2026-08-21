package com.ofss.project.service;

import com.ofss.project.dto.response.CollectionActionResponse;
import com.ofss.project.entity.CollectionAction;
import com.ofss.project.entity.CreditCardBill;
import com.ofss.project.enums.CollectionActionType;
import com.ofss.project.enums.DpdBucket;
import com.ofss.project.repository.CollectionActionRepository;
import com.ofss.project.repository.CreditCardBillRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionsActionService {

    private final CreditCardBillRepository billRepository;
    private final CollectionActionRepository actionRepository;

    @Transactional
    public CollectionActionResponse createAction(
            Long billId,
            CollectionActionType actionType,
            String remarks) {

        CreditCardBill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException(
                        "Bill not found"));

        validateAction(
                bill.getDpdBucket(),
                actionType);

        CollectionAction action = CollectionAction.builder()
                .bill(bill)
                .actionType(actionType)
                .remarks(
                        remarks != null
                                ? remarks.trim()
                                : null)
                .build();

        CollectionAction saved = actionRepository.save(action);

        return toResponse(saved);
    }

    public List<CollectionActionResponse> getActions(
            Long billId) {

        return actionRepository
                .findByBill_IdOrderByActionDateAsc(
                        billId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void validateAction(
            DpdBucket bucket,
            CollectionActionType actionType) {

        switch (bucket) {

            case SMA_0:
            case SMA_1:

                if (actionType != CollectionActionType.REMINDER) {

                    throw new IllegalStateException(
                            "Only reminder actions are allowed for SMA-0 and SMA-1");
                }

                break;

            case SMA_2:

                if (actionType != CollectionActionType.CALL) {

                    throw new IllegalStateException(
                            "Only call actions are allowed for SMA-2");
                }

                break;

            case SUB_STANDARD:
            case DOUBTFUL:

                if (actionType != CollectionActionType.LEGAL_NOTICE
                        && actionType != CollectionActionType.SETTLEMENT_OFFER
                        && actionType != CollectionActionType.RESTRUCTURING) {

                    throw new IllegalStateException(
                            "Invalid collection action for this bucket");
                }

                break;

            case LOSS:

                if (actionType != CollectionActionType.WRITE_OFF) {

                    throw new IllegalStateException(
                            "Only write-off action is allowed for LOSS accounts");
                }

                break;

            case STANDARD:

                throw new IllegalStateException(
                        "Collection actions are not allowed for Standard accounts");

            default:

                throw new IllegalStateException(
                        "Unsupported DPD bucket: " + bucket);
        }
    }

    private CollectionActionResponse toResponse(
            CollectionAction action) {

        return new CollectionActionResponse(
                action.getId(),
                action.getBill().getId(),
                action.getActionType(),
                action.getRemarks(),
                action.getActionDate());
    }
}