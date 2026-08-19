package com.ofss.project.rag;

import java.util.List;

public record RagChatResponse(String question,
        List<RagRecommendation> recommendations, String answer,
        String disclaimer) {
}
