package com.ofss.project.dto.response;

import com.ofss.project.enums.ApplicationDocumentType;
import com.ofss.project.enums.DocumentVerificationStatus;

import java.time.LocalDateTime;

public record ApplicationDocumentResponse(

        Long id,

        ApplicationDocumentType documentType,

        String originalFileName,

        String contentType,

        Long fileSize,

        DocumentVerificationStatus verificationStatus,

        LocalDateTime uploadedAt,

        LocalDateTime verifiedAt
) {
}