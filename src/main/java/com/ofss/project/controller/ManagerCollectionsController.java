package com.ofss.project.controller;

import com.ofss.project.dto.request.CollectionActionRequest;
import com.ofss.project.dto.response.CollectionActionResponse;
import com.ofss.project.service.CollectionsActionService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/manager/collections")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class ManagerCollectionsController {

    private final CollectionsActionService actionService;

    @PostMapping("/{billId}/actions")
    public ResponseEntity<CollectionActionResponse> createAction(
            @PathVariable Long billId,
            @Valid @RequestBody CollectionActionRequest request
    ) {

        return ResponseEntity.ok(
                actionService.createAction(
                        billId,
                        request.actionType(),
                        request.remarks()
                )
        );
    }

    @GetMapping("/{billId}/actions")
    public ResponseEntity<List<CollectionActionResponse>> getActions(
            @PathVariable Long billId
    ) {

        return ResponseEntity.ok(
                actionService.getActions(billId)
        );
    }
}