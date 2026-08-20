package com.ofss.project.entity;

import com.ofss.project.enums.BillStatus;
import com.ofss.project.enums.DpdBucket;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "CREDIT_CARD_BILLS",
        indexes = {
                @Index(
                        name = "IDX_BILL_CARD",
                        columnList = "CARD_ID"
                ),
                @Index(
                        name = "IDX_BILL_DUE_DATE",
                        columnList = "DUE_DATE"
                ),
                @Index(
                        name = "IDX_BILL_DPD_BUCKET",
                        columnList = "DPD_BUCKET"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCardBill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "CARD_ID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_BILL_CARD"
            )
    )
    private CreditCard creditCard;

    @Column(
            name = "BILLING_DATE",
            nullable = false
    )
    private LocalDate billingDate;

    @Column(
            name = "DUE_DATE",
            nullable = false
    )
    private LocalDate dueDate;

    @Column(
            name = "TOTAL_OUTSTANDING",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal totalOutstanding;

    @Column(
            name = "MINIMUM_AMOUNT_DUE",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal minimumAmountDue;

    @Column(
            name = "INTEREST_AMOUNT",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal interestAmount;

    @Column(
            name = "LATE_FEE",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal lateFee;

    @Column(
            name = "PAID_AMOUNT",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal paidAmount;

    @Column(
            name = "REMAINING_AMOUNT",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal remainingAmount;

    @Column(
            name = "DPD",
            nullable = false
    )
    private Integer dpd;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "DPD_BUCKET",
            nullable = false,
            length = 30
    )
    private DpdBucket dpdBucket;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "STATUS",
            nullable = false,
            length = 30
    )
    private BillStatus status;

    @Column(
            name = "CREATED_AT",
            nullable = false
    )
    private LocalDateTime createdAt;

    @Column(
            name = "UPDATED_AT"
    )
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now =
                LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                LocalDateTime.now();
    }
}