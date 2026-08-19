package com.ofss.project.controller;

import com.ofss.project.dto.response.ApplicationRiskResponse;
import com.ofss.project.dto.response.CardIssuanceResponse;
import com.ofss.project.service.CreditOfficerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/credit-officer")
@PreAuthorize("hasAnyRole('MANAGER', 'CREDIT_OFFICER')")
public class CreditofficerController {

    private final CreditOfficerService creditOfficerService;

    public CreditofficerController(
            CreditOfficerService creditOfficerService
    ) {
        this.creditOfficerService = creditOfficerService;
    }


    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationRiskResponse>> getApplications() {
        return ResponseEntity.ok(
                creditOfficerService.getAllApplications()
        );
    }

   
    @GetMapping("/applications/{id}")
    public ResponseEntity<ApplicationRiskResponse> getApplication(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                creditOfficerService.getApplicationDetails(id)
        );
    }

    @PostMapping("/applications/{id}/approve")
    public ResponseEntity<CardIssuanceResponse> approve(
            @PathVariable Long id
    ) {
    
        return ResponseEntity.ok(
                creditOfficerService.approveApplication(id)
        );
    }

   
    @PostMapping("/applications/{id}/reject")
    public ResponseEntity<Void> reject(
            @PathVariable Long id
    ) {
        creditOfficerService.rejectApplication(id);
        return ResponseEntity.ok().build();
    }
}