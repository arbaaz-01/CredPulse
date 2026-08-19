package com.ofss.project.rag;

public record RagRecommendation(String cardName, String productCode,
        String reason, double score) {
}
