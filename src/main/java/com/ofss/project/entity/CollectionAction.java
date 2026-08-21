package com.ofss.project.entity;

import com.ofss.project.enums.CollectionActionType;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "COLLECTION_ACTIONS",
        indexes = {
                @Index(
                        name = "IDX_COLLECTION_ACTION_BILL",
                        columnList = "BILL_ID"
                ),
                @Index(
                        name = "IDX_COLLECTION_ACTION_DATE",
                        columnList = "ACTION_DATE"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollectionAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "BILL_ID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_COLLECTION_ACTION_BILL"
            )
    )
    private CreditCardBill bill;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "ACTION_TYPE",
            nullable = false,
            length = 30
    )
    private CollectionActionType actionType;

    @Column(
            name = "REMARKS",
            length = 500
    )
    private String remarks;

    @Column(
            name = "ACTION_DATE",
            nullable = false
    )
    private LocalDateTime actionDate;

    @PrePersist
    protected void onCreate() {
        actionDate = LocalDateTime.now();
    }
}