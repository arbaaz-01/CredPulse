package com.ofss.project.entity;

import com.ofss.project.enums.ApplicationStatus;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "APPLICATION_STATUS_HISTORY",
        indexes = {
                @Index(
                        name = "IDX_STATUS_HISTORY_APPLICATION",
                        columnList = "APPLICATION_ID"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "APPLICATION_ID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_STATUS_HISTORY_APPLICATION"
            )
    )
    private CreditCardApplication application;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "OLD_STATUS",
            length = 30
    )
    private ApplicationStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "NEW_STATUS",
            nullable = false,
            length = 30
    )
    private ApplicationStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "CHANGED_BY",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_STATUS_HISTORY_USER"
            )
    )
    private User changedBy;

    @Column(
            name = "REMARKS",
            length = 500
    )
    private String remarks;

    @Column(
            name = "CHANGED_AT",
            nullable = false
    )
    private LocalDateTime changedAt;

    @PrePersist
    protected void onCreate() {
        changedAt = LocalDateTime.now();
    }
}