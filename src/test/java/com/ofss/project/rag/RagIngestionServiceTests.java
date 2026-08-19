package com.ofss.project.rag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.jdbc.core.JdbcTemplate;

class RagIngestionServiceTests {

    @Test
    void rebuildsTheVectorTableBeforeAddingOnlyCurrentCardDocuments() {
        VectorStore vectorStore = mock(VectorStore.class);
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        RagIngestionService service = new RagIngestionService(vectorStore, new RagMetadataResolver(), jdbcTemplate);

        RagIngestionResponse response = service.ingest();

        verify(jdbcTemplate).update("DELETE FROM CREDPULSE_RAG_VECTORS");
        ArgumentCaptor<List<Document>> documents = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(documents.capture());

        List<Document> indexed = documents.getValue();
        Set<String> cardCodes = indexed.stream().map(Document::getMetadata)
                .map(metadata -> metadata.get("cardCode"))
                .filter(java.util.Objects::nonNull).map(Object::toString).collect(java.util.stream.Collectors.toSet());
        Document gold = indexed.stream().filter(document -> "CC_GOLD_003".equals(document.getMetadata().get("cardCode")))
                .findFirst().orElseThrow();

        assertEquals("SUCCESS", response.status());
        assertEquals(Set.of(
                "CC_BASIC_001", "CC_SILVER_002", "CC_GOLD_003", "CC_PLATINUM_004", "CC_TRAVEL_005",
                "CC_FUEL_006", "CC_CASHBACK_007", "CC_SHOP_008", "CC_DINING_009", "CC_CORPORATE_010",
                "CC_STUDENT_011", "CC_PREMIUM_012", "CC_LIFESTYLE_013", "CC_ELITE_014", "CC_STARTER_015"),
                cardCodes);
        assertFalse(cardCodes.stream().anyMatch(code -> code.startsWith("CP")));
        assertTrue(gold.getText().contains("Product code: CC_GOLD_003"));
    }
}
