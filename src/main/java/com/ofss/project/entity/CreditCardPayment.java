package com.ofss.project.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ofss.project.enums.PaymentType;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(
        name = "CREDIT_CARD_PAYMENTS",
        indexes = {
                @Index(
                        name = "IDX_PAYMENT_BILL",
                        columnList = "BILL_ID"
                ),
                @Index(
                        name = "IDX_PAYMENT_DATE",
                        columnList = "PAYMENT_DATE"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCardPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "BILL_ID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_PAYMENT_BILL"
            )
    )
    private CreditCardBill bill;

    @Column(
            name = "PAYMENT_AMOUNT",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal paymentAmount;

    @Column(
            name = "PAYMENT_DATE",
            nullable = false
    )
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "PAYMENT_TYPE",
            nullable = false,
            length = 20
    )
    private PaymentType paymentType;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}