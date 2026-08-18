package com.ofss.project.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ofss.project.dto.response.ApplicationDocumentResponse;
import com.ofss.project.entity.ApplicationDocument;
import com.ofss.project.entity.CreditCardApplication;
import com.ofss.project.enums.ApplicationDocumentType;
import com.ofss.project.enums.ApplicationStatus;
import com.ofss.project.enums.DocumentVerificationStatus;
import com.ofss.project.repository.ApplicationDocumentRepository;
import com.ofss.project.repository.CreditCardApplicationRepository;
import com.ofss.project.security.CurrentUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationDocumentService {

    private final ApplicationDocumentRepository documentRepository;
    private final CreditCardApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;
    private final CurrentUser currentUser;

    @Transactional
    public ApplicationDocumentResponse uploadDocument(
            Long applicationId,
            ApplicationDocumentType documentType,
            MultipartFile file) {

        CreditCardApplication application =
                getOwnedApplication(applicationId);

        ensureDraft(application);

        FileStorageService.StoredFile storedFile =
                fileStorageService.store(
                        file,
                        applicationId,
                        documentType.name()
                );

        ApplicationDocument existing =
                documentRepository
                        .findByApplication_IdAndDocumentType(
                                applicationId,
                                documentType
                        )
                        .orElse(null);

        if (existing != null) {

            String oldPath =
                    existing.getStoragePath();

            existing.setOriginalFileName(
                    storedFile.originalFileName()
            );
            existing.setStoredFileName(
                    storedFile.storedFileName()
            );
            existing.setStoragePath(
                    storedFile.storagePath()
            );
            existing.setContentType(
                    storedFile.contentType()
            );
            existing.setFileSize(
                    storedFile.fileSize()
            );
            existing.setVerificationStatus(
                    DocumentVerificationStatus.PENDING
            );
            existing.setVerifiedAt(null);

            ApplicationDocument saved =
                    documentRepository.save(existing);

            if (!oldPath.equals(
                    storedFile.storagePath())) {

                fileStorageService.delete(oldPath);
            }

            return toResponse(saved);
        }

        ApplicationDocument document =
                ApplicationDocument.builder()
                        .application(application)
                        .documentType(documentType)
                        .originalFileName(
                                storedFile.originalFileName()
                        )
                        .storedFileName(
                                storedFile.storedFileName()
                        )
                        .storagePath(
                                storedFile.storagePath()
                        )
                        .contentType(
                                storedFile.contentType()
                        )
                        .fileSize(
                                storedFile.fileSize()
                        )
                        .verificationStatus(
                                DocumentVerificationStatus.PENDING
                        )
                        .build();

        return toResponse(
                documentRepository.save(document)
        );
    }

    @Transactional(readOnly = true)
    public List<ApplicationDocumentResponse>
    getDocuments(Long applicationId) {

        getOwnedApplication(applicationId);

        return documentRepository
                .findByApplication_IdOrderByUploadedAtAsc(
                        applicationId
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private CreditCardApplication
    getOwnedApplication(Long applicationId) {

        Long userId =
                currentUser.getUserId();

        return applicationRepository
                .findByIdAndUser_Id(
                        applicationId,
                        userId
                )
                .orElseThrow(() ->
                        new RuntimeException(
                                "Credit card application not found"
                        )
                );
    }

    private void ensureDraft(
            CreditCardApplication application) {

        if (application.getStatus()
                != ApplicationStatus.DRAFT) {

            throw new IllegalStateException(
                    "Documents can only be uploaded to DRAFT applications"
            );
        }
    }

    private ApplicationDocumentResponse
    toResponse(ApplicationDocument document) {

        return new ApplicationDocumentResponse(
                document.getId(),
                document.getDocumentType(),
                document.getOriginalFileName(),
                document.getContentType(),
                document.getFileSize(),
                document.getVerificationStatus(),
                document.getUploadedAt(),
                document.getVerifiedAt()
        );
    }
}