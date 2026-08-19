package com.ofss.project.controller;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ofss.project.rag.RagChatRequest;
import com.ofss.project.rag.RagChatResponse;
import com.ofss.project.rag.RagIngestionResponse;
import com.ofss.project.rag.RagIngestionService;
import com.ofss.project.rag.RagService;
import com.ofss.project.rag.RagUnavailableException;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rag")
@RequiredArgsConstructor
public class RagController {
    private final ObjectProvider<RagService> ragService;
    private final ObjectProvider<RagIngestionService> ingestionService;

    @PostMapping("/chat")
    public ResponseEntity<RagChatResponse> chat(@Valid @RequestBody RagChatRequest request) {
        return ResponseEntity.ok(service().chat(request));
    }

    @PostMapping("/admin/ingest")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RagIngestionResponse> ingest() {
        RagIngestionService service = ingestionService.getIfAvailable();
        if (service == null) {
            throw new RagUnavailableException("RAG is disabled. Set RAG_ENABLED=true after configuring the AI provider.");
        }
        return ResponseEntity.ok(service.ingest());
    }

    @GetMapping("/admin/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.Map<String, String>> status() {
        boolean configured = ragService.getIfAvailable() != null;
        return ResponseEntity.ok(java.util.Map.of(
                "rag", configured ? "ENABLED" : "DISABLED",
                "vectorStore", configured ? "CONFIGURED" : "NOT_CONFIGURED",
                "chatModel", configured ? "CONFIGURED" : "NOT_CONFIGURED"));
    }

    private RagService service() {
        RagService service = ragService.getIfAvailable();
        if (service == null) {
            throw new RagUnavailableException("RAG is disabled. Set RAG_ENABLED=true after configuring the AI provider.");
        }
        return service;
    }
}
