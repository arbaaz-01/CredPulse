package com.ofss.project.rag;

public record RagIngestionResponse(String status, int documentsProcessed,
        int chunksIndexed) {
}
