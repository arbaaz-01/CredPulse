package com.ofss.project.scheduler;

import com.ofss.project.entity.CreditCardBill;
import com.ofss.project.enums.BillStatus;
import com.ofss.project.repository.CreditCardBillRepository;
import com.ofss.project.service.DpdService;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DpdScheduler {

    private final CreditCardBillRepository billRepository;
    private final DpdService dpdService;

    @Scheduled(cron = "0 0 1 * * *")
    public void processDpd() {

        LocalDate today = LocalDate.now();

        List<CreditCardBill> bills =
                billRepository.findByDueDateBeforeAndStatusIn(
                        today,
                        List.of(
                                BillStatus.UNPAID,
                                BillStatus.PARTIALLY_PAID,
                                BillStatus.OVERDUE
                        )
                );

        for (CreditCardBill bill : bills) {

            dpdService.updateDpd(
                    bill.getId(),
                    today
            );
        }
    }
}