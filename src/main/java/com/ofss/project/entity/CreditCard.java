package com.ofss.project.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ofss.project.enums.CardStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "CREDIT_CARDS",
    indexes = {
        @Index(
            name = "IDX_CREDIT_CARD_USER",
            columnList = "USER_ID"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "USER_ID",
        nullable = false,
        foreignKey = @ForeignKey(
            name = "FK_CREDIT_CARD_USER"
        )
    )
    private User user;

    @Column(
        name = "CARD_NUMBER_HASH",
        nullable = false,
        length = 64
    )
    private String cardNumberHash;

    @Column(
        name = "CARD_LAST_FOUR",
        nullable = false,
        length = 4
    )
    private String cardLastFour;

    @Column(
        name = "CARD_HOLDER_NAME",
        nullable = false,
        length = 100
    )
    private String cardHolderName;

    @Column(
        name = "EXPIRY_MONTH",
        nullable = false
    )
    private Integer expiryMonth;

    @Column(
        name = "EXPIRY_YEAR",
        nullable = false
    )
    private Integer expiryYear;

    @Column(
        name = "CREDIT_LIMIT",
        nullable = false,
        precision = 15,
        scale = 2
    )
    private BigDecimal creditLimit;

    @Column(
        name = "AVAILABLE_LIMIT",
        nullable = false,
        precision = 15,
        scale = 2
    )
    private BigDecimal availableLimit;

    @Enumerated(EnumType.STRING)
    @Column(
        name = "STATUS",
        nullable = false,
        length = 20
    )
    private CardStatus status;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
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
