package com.ofss.project.controller;

import com.ofss.project.dto.request.CreateCreditCardApplicationRequest;
import com.ofss.project.dto.request.UpdateCreditCardApplicationRequest;
import com.ofss.project.dto.response.CreditCardApplicationResponse;
import com.ofss.project.service.CreditCardApplicationService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/card-applications")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class CreditCardApplicationController {

	private final CreditCardApplicationService applicationService;

	@PostMapping
	public ResponseEntity<CreditCardApplicationResponse> createDraft(
			@Valid @RequestBody CreateCreditCardApplicationRequest request) {

		return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.createDraft(request));
	}

	@GetMapping
	public ResponseEntity<List<CreditCardApplicationResponse>> getMyApplications() {

		return ResponseEntity.ok(applicationService.getMyApplications());
	}

	@GetMapping("/{id}")
	public ResponseEntity<CreditCardApplicationResponse> getMyApplication(@PathVariable Long id) {

		return ResponseEntity.ok(applicationService.getMyApplication(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<CreditCardApplicationResponse> updateApplication(@PathVariable Long id,
			@Valid @RequestBody UpdateCreditCardApplicationRequest request) {

		return ResponseEntity.ok(applicationService.updateApplication(id, request));
	}

	@PostMapping("/{id}/submit")
	public ResponseEntity<CreditCardApplicationResponse> submitApplication(@PathVariable Long id) {

		return ResponseEntity.ok(applicationService.submitApplication(id));
	}
}