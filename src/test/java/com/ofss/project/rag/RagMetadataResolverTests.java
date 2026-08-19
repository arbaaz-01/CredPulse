package com.ofss.project.rag;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

class RagMetadataResolverTests {
    private final RagMetadataResolver resolver = new RagMetadataResolver();

    @Test
    void createsCardMetadataUsingRelationalGoldProductIdentity() {
        Map<String, Object> metadata = resolver.resolve("gold-credit-card.txt");
        assertEquals("card", metadata.get("category"));
        assertEquals("CC_GOLD_003", metadata.get("cardCode"));
        assertEquals("Gold Credit Card", metadata.get("cardName"));
        assertEquals("card_benefits", metadata.get("documentType"));
    }

    @Test
    void createsPolicyMetadataForEligibilityKnowledge() {
        Map<String, Object> metadata = resolver.resolve("eligibility-rules.txt");
        assertEquals("policy", metadata.get("category"));
        assertEquals("eligibility", metadata.get("documentType"));
    }
}
