package com.ofss.project.entity;

import com.ofss.project.enums.NotificationType;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "NOTIFICATIONS",
        indexes = {
                @Index(
                        name = "IDX_NOTIFICATION_USER",
                        columnList = "USER_ID"
                ),
                @Index(
                        name = "IDX_NOTIFICATION_USER_READ",
                        columnList = "USER_ID,READ_STATUS"
                ),
                @Index(
                        name = "IDX_NOTIFICATION_CREATED_AT",
                        columnList = "CREATED_AT"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "USER_ID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_NOTIFICATION_USER"
            )
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "APPLICATION_ID",
            foreignKey = @ForeignKey(
                    name = "FK_NOTIFICATION_APPLICATION"
            )
    )
    private CreditCardApplication application;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "TYPE",
            nullable = false,
            length = 30
    )
    private NotificationType type;

    @Column(
            name = "TITLE",
            nullable = false,
            length = 150
    )
    private String title;

    @Column(
            name = "MESSAGE",
            nullable = false,
            length = 500
    )
    private String message;

    @Column(
            name = "READ_STATUS",
            nullable = false
    )
    private boolean readStatus;

    @Column(
            name = "READ_AT"
    )
    private LocalDateTime readAt;

    @Column(
            name = "CREATED_AT",
            nullable = false
    )
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}