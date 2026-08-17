package com.ofss.project.entity;

import com.ofss.project.enums.CreditCardProductStatus;

import jakarta.persistence.*;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "CREDIT_CARD_PRODUCTS",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_CARD_PRODUCT_CODE",
                        columnNames = "PRODUCT_CODE"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCardProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "PRODUCT_CODE",
            nullable = false,
            length = 30
    )
    private String productCode;

    @Column(
            name = "NAME",
            nullable = false,
            length = 100
    )
    private String name;

    @Column(
            name = "DESCRIPTION",
            length = 500
    )
    private String description;

    @Column(
            name = "MINIMUM_INCOME",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal minimumIncome;

    @Column(
            name = "ANNUAL_FEE",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal annualFee;

    @Column(
            name = "INTEREST_RATE",
            nullable = false,
            precision = 7,
            scale = 2
    )
    private BigDecimal interestRate;

    @Column(
            name = "MIN_CREDIT_LIMIT",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal minCreditLimit;

    @Column(
            name = "MAX_CREDIT_LIMIT",
            nullable = false,
            precision = 19,
            scale = 2
    )
    private BigDecimal maxCreditLimit;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "STATUS",
            nullable = false,
            length = 20
    )
    private CreditCardProductStatus status;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}