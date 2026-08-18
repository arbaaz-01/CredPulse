package com.ofss.project.controller;

import com.ofss.project.service.ApplicationDocumentAccessService;
import com.ofss.project.service.ApplicationDocumentAccessService.DocumentFile;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/card-applications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'CREDIT_OFFICER', 'MANAGER')")
public class ApplicationDocumentAccessController {

    private final ApplicationDocumentAccessService documentAccessService;

    @GetMapping("/{applicationId}/documents/{documentId}/view")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long applicationId,
            @PathVariable Long documentId) {

        DocumentFile document =
                documentAccessService.getDocument(
                        applicationId,
                        documentId
                );

        MediaType mediaType =
                resolveMediaType(
                        document.contentType()
                );

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .contentLength(document.fileSize())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition
                                .inline()
                                .filename(
                                        sanitizeFileName(
                                                document.originalFileName()
                                        )
                                )
                                .build()
                                .toString()
                )
                .body(document.resource());
    }

    @GetMapping("/{applicationId}/documents/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long applicationId,
            @PathVariable Long documentId) {

        DocumentFile document =
                documentAccessService.getDocument(
                        applicationId,
                        documentId
                );

        MediaType mediaType =
                resolveMediaType(
                        document.contentType()
                );

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .contentLength(document.fileSize())
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition
                                .attachment()
                                .filename(
                                        sanitizeFileName(
                                                document.originalFileName()
                                        )
                                )
                                .build()
                                .toString()
                )
                .body(document.resource());
    }

    private MediaType resolveMediaType(
            String contentType) {

        if ("application/pdf".equalsIgnoreCase(
                contentType
        )) {
            return MediaType.APPLICATION_PDF;
        }

        return MediaType.APPLICATION_OCTET_STREAM;
    }

    private String sanitizeFileName(
            String fileName) {

        if (fileName == null ||
                fileName.isBlank()) {

            return "document.pdf";
        }

        return fileName
                .replace("\\", "_")
                .replace("/", "_")
                .replace("\"", "_");
    }
}