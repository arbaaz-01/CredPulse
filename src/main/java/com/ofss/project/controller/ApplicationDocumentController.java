package com.ofss.project.controller;

import com.ofss.project.dto.response.ApplicationDocumentResponse;
import com.ofss.project.enums.ApplicationDocumentType;
import com.ofss.project.service.ApplicationDocumentService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/card-applications")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'CREDIT_OFFICER')")
public class ApplicationDocumentController {

	private final ApplicationDocumentService documentService;

	@PostMapping(value = "/{applicationId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApplicationDocumentResponse> uploadDocument(@PathVariable Long applicationId,

			@RequestParam ApplicationDocumentType documentType,

			@RequestPart("file") MultipartFile file) {

		ApplicationDocumentResponse response = documentService.uploadDocument(applicationId, documentType, file);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{applicationId}/documents")
	public ResponseEntity<List<ApplicationDocumentResponse>> getDocuments(@PathVariable Long applicationId) {

		return ResponseEntity.ok(documentService.getDocuments(applicationId));
	}
}