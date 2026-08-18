package com.ofss.project.entity;

import com.ofss.project.enums.ApplicationDocumentType;
import com.ofss.project.enums.DocumentVerificationStatus;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "APPLICATION_DOCUMENTS",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "UK_APPLICATION_DOCUMENT_TYPE",
                        columnNames = {
                                "APPLICATION_ID",
                                "DOCUMENT_TYPE"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "IDX_DOCUMENT_APPLICATION",
                        columnList = "APPLICATION_ID"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "APPLICATION_ID",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "FK_DOCUMENT_APPLICATION"
            )
    )
    private CreditCardApplication application;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "DOCUMENT_TYPE",
            nullable = false,
            length = 30
    )
    private ApplicationDocumentType documentType;

    @Column(
            name = "ORIGINAL_FILE_NAME",
            nullable = false,
            length = 255
    )
    private String originalFileName;

    @Column(
            name = "STORED_FILE_NAME",
            nullable = false,
            length = 255
    )
    private String storedFileName;

    @Column(
            name = "STORAGE_PATH",
            nullable = false,
            length = 1000
    )
    private String storagePath;

    @Column(
            name = "CONTENT_TYPE",
            nullable = false,
            length = 100
    )
    private String contentType;

    @Column(
            name = "FILE_SIZE",
            nullable = false
    )
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "VERIFICATION_STATUS",
            nullable = false,
            length = 20
    )
    private DocumentVerificationStatus verificationStatus;

    @Column(
            name = "UPLOADED_AT",
            nullable = false
    )
    private LocalDateTime uploadedAt;

    @Column(name = "VERIFIED_AT")
    private LocalDateTime verifiedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}