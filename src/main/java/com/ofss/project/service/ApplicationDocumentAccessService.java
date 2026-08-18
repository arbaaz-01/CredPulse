package com.ofss.project.service;

import com.ofss.project.entity.ApplicationDocument;
import com.ofss.project.entity.CreditCardApplication;
import com.ofss.project.enums.ApplicationStatus;
import com.ofss.project.exception.DocumentAccessDeniedException;
import com.ofss.project.exception.DocumentNotFoundException;
import com.ofss.project.repository.ApplicationDocumentRepository;
import com.ofss.project.repository.CreditCardApplicationRepository;
import com.ofss.project.security.CurrentUser;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationDocumentAccessService {

    private final ApplicationDocumentRepository documentRepository;
    private final CreditCardApplicationRepository applicationRepository;
    private final FileStorageService fileStorageService;
    private final CurrentUser currentUser;

    @Transactional(readOnly = true)
    public DocumentFile getDocument(
            Long applicationId,
            Long documentId) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new DocumentAccessDeniedException(
                    "Authentication is required"
            );
        }

        CreditCardApplication application =
                applicationRepository
                        .findById(applicationId)
                        .orElseThrow(() ->
                                new DocumentNotFoundException(
                                        "Credit card application not found"
                                )
                        );

        ApplicationDocument document =
                documentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new DocumentNotFoundException(
                                        "Document not found"
                                )
                        );

        if (!document.getApplication()
                .getId()
                .equals(application.getId())) {

            throw new DocumentNotFoundException(
                    "Document not found"
            );
        }

        validateAccess(
                application,
                authentication
        );

        Resource resource =
                fileStorageService.loadAsResource(
                        document.getStoragePath()
                );

        return new DocumentFile(
                resource,
                document.getOriginalFileName(),
                document.getContentType(),
                document.getFileSize()
        );
    }

    private void validateAccess(
            CreditCardApplication application,
            Authentication authentication) {

        boolean isUser =
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals(
                                                        "ROLE_USER"
                                                )
                        );

        boolean isCreditOfficer =
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals(
                                                        "ROLE_CREDIT_OFFICER"
                                                )
                        );

        boolean isManager =
                authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals(
                                                        "ROLE_MANAGER"
                                                )
                        );

        if (isUser) {

            Long currentUserId =
                    currentUser.getUserId();

            if (!application
                    .getUser()
                    .getId()
                    .equals(currentUserId)) {

                throw new DocumentAccessDeniedException(
                        "You do not have access to this document"
                );
            }

            return;
        }

        if (isCreditOfficer || isManager) {

            if (!isReviewableApplication(
                    application
            )) {

                throw new DocumentAccessDeniedException(
                        "This application is not available for document review"
                );
            }

            return;
        }

        throw new DocumentAccessDeniedException(
                "You do not have permission to access documents"
        );
    }

    private boolean isReviewableApplication(
            CreditCardApplication application) {

        ApplicationStatus status =
                application.getStatus();

        return status == ApplicationStatus.SUBMITTED
                || status == ApplicationStatus.UNDER_REVIEW
                || status == ApplicationStatus.ADDITIONAL_INFO_REQUIRED
                || status == ApplicationStatus.APPROVED
                || status == ApplicationStatus.REJECTED;
    }

    public record DocumentFile(
            Resource resource,
            String originalFileName,
            String contentType,
            Long fileSize
    ) {
    }
}