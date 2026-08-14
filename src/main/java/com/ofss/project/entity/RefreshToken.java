package com.ofss.project.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "REFRESH_TOKENS",
    indexes = {
        @Index(
            name = "IDX_REFRESH_TOKEN_HASH",
            columnList = "TOKEN_HASH"
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "USER_ID",
        nullable = false,
        foreignKey = @ForeignKey(name = "FK_REFRESH_TOKEN_USER")
    )
    private User user;

    @Column(
        name = "TOKEN_HASH",
        nullable = false,
        unique = true,
        length = 64
    )
    private String tokenHash;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "REVOKED", nullable = false)
    private boolean revoked;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
