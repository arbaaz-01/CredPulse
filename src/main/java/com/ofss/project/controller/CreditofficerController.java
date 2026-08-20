package com.ofss.project.controller;

import com.ofss.project.dto.request.ApplicationDecisionRequest;
import com.ofss.project.dto.request.ApplicationRejectRequest;
import com.ofss.project.dto.response.ApplicationRiskResponse;
import com.ofss.project.dto.response.ApplicationDocumentResponse;
import com.ofss.project.dto.response.CardIssuanceResponse;
import com.ofss.project.service.CreditOfficerService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/credit-officer")
@PreAuthorize("hasAnyRole('MANAGER', 'CREDIT_OFFICER')")
public class CreditofficerController {

        private final CreditOfficerService creditOfficerService;

        public CreditofficerController(
                        CreditOfficerService creditOfficerService) {
                this.creditOfficerService = creditOfficerService;
        }

        @GetMapping("/applications")
        public ResponseEntity<List<ApplicationRiskResponse>> getApplications() {
                return ResponseEntity.ok(
                                creditOfficerService.getAllApplications());
        }

        @GetMapping("/applications/{id}")
        public ResponseEntity<ApplicationRiskResponse> getApplication(
                        @PathVariable Long id) {
                return ResponseEntity.ok(
                                creditOfficerService.getApplicationDetails(id));
        }

        @GetMapping("/applications/{id}/documents")
        public ResponseEntity<List<ApplicationDocumentResponse>> getApplicationDocuments(
                        @PathVariable Long id) {
                return ResponseEntity.ok(
                                creditOfficerService.getApplicationDocuments(id));
        }

        @PostMapping("/applications/{id}/approve")
        public ResponseEntity<CardIssuanceResponse> approve(
                        @PathVariable Long id) {

                return ResponseEntity.ok(
                                creditOfficerService.approveApplication(id));
        }

        @PostMapping("/applications/{id}/reject")
        public ResponseEntity<Void> reject(
                        @PathVariable Long id,
                        @Valid @RequestBody ApplicationRejectRequest request,
                        @AuthenticationPrincipal Jwt jwt) {

                Long officerUserId = Long.valueOf(jwt.getSubject());

                creditOfficerService.rejectApplication(
                                id,
                                officerUserId,
                                request.remark());

                return ResponseEntity.ok().build();
        }

}
