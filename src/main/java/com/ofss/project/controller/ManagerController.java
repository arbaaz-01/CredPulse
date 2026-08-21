package com.ofss.project.controller;

import com.ofss.project.dto.response.CollectionAccountResponse;
import com.ofss.project.dto.response.ManagerRiskSummaryResponse;
import com.ofss.project.service.CollectionsService;
import com.ofss.project.service.ManagerService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manager")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER', 'USER')")
public class ManagerController {

    private final CollectionsService collectionsService;
    private final ManagerService managerService;

    @GetMapping("/collections/delinquent")
    public ResponseEntity<List<CollectionAccountResponse>> getDelinquentAccounts() {

        return ResponseEntity.ok(
                collectionsService.getDelinquentBills());
    }

    @GetMapping("/collections/npa")
    public ResponseEntity<List<CollectionAccountResponse>> getNpaAccounts() {

        return ResponseEntity.ok(
                collectionsService.getNpaBills());
    }

    @GetMapping("/risk/summary")
    public ResponseEntity<ManagerRiskSummaryResponse> getRiskSummary() {

        return ResponseEntity.ok(
                managerService.getRiskSummary());
    }
}