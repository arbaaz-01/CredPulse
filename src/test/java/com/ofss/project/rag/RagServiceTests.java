package com.ofss.project.rag;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.util.ReflectionTestUtils;

import com.ofss.project.repository.CreditCardProductRepository;
import com.ofss.project.security.CurrentUser;
import com.ofss.project.entity.CreditCardProduct;

class RagServiceTests {

    @Test
    void answersGoldBenefitsWithoutFinancialInputsUsingRetrievedContext() {
        VectorStore vectorStore = mock(VectorStore.class);
        ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(Document.builder()
                .text("Gold Credit Card has 2% fictional cashback on eligible dining and shopping.")
                .metadata(java.util.Map.of("cardCode", "CC_GOLD_003")).build()));
        when(chatClientBuilder.build().prompt().system(anyString()).user(anyString()).call().content())
                .thenReturn("Gold Credit Card provides fictional dining and shopping cashback.");

        RagService service = service(vectorStore, chatClientBuilder, List.of());
        RagChatResponse response = service.chat(new RagChatRequest("What are the benefits of the Gold Credit Card?",
                null, null, null, null, null, null, null));

        verify(vectorStore).similaritySearch(any(SearchRequest.class));
        assertFalse(response.answer().contains("Please provide monthly income"));
        assertTrue(response.answer().contains("Gold Credit Card"));
    }

    @Test
    void answersPlatinumFeeWithoutFinancialInputsUsingRetrievedContext() {
        VectorStore vectorStore = mock(VectorStore.class);
        ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(Document.builder()
                .text("Platinum Credit Card annual fee is 2499.")
                .metadata(java.util.Map.of("cardCode", "CC_PLATINUM_004")).build()));
        when(chatClientBuilder.build().prompt().system(anyString()).user(anyString()).call().content())
                .thenReturn("The Platinum Credit Card annual fee is 2499.");

        RagService service = service(vectorStore, chatClientBuilder, List.of());
        RagChatResponse response = service.chat(new RagChatRequest("What is the annual fee of the Platinum Credit Card?",
                null, null, null, null, null, null, null));

        verify(vectorStore).similaritySearch(any(SearchRequest.class));
        assertFalse(response.answer().contains("Please provide monthly income"));
    }

    @Test
    void asksForFinancialInputsOnlyForRecommendationQuestions() {
        RagService service = service(mock(VectorStore.class), mock(ChatClient.Builder.class), List.of());

        RagChatResponse response = service.chat(new RagChatRequest("Which card is best for me?",
                null, null, null, null, null, null, null));

        assertTrue(response.answer().toLowerCase().contains("monthly income"));
        assertTrue(response.answer().toLowerCase().contains("monthly expense"));
    }

    @Test
    void doesNotInventAnAnswerWhenNoInformationIsRetrieved() {
        VectorStore vectorStore = mock(VectorStore.class);
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of());
        RagService service = service(vectorStore, mock(ChatClient.Builder.class), List.of());

        RagChatResponse response = service.chat(new RagChatRequest("What are the benefits of the Imaginary Card?",
                null, null, null, null, null, null, null));

        assertTrue(response.answer().contains("does not contain enough relevant information"));
    }

    @Test
    void returnsNoRecommendationWhenIncomeExcludesAllActiveProducts() {
        RagService service = service(mock(VectorStore.class), mock(ChatClient.Builder.class), List.of());

        RagChatResponse response = service.chat(new RagChatRequest("Which card can I get?",
                BigDecimal.valueOf(25000), BigDecimal.valueOf(10000), null, null, null, null, null));

        assertTrue(response.recommendations().isEmpty());
        assertTrue(response.answer().contains("No active CredPulse card"));
    }

    @Test
    void returnsEligibleRecommendationsForPersonalizedRequests() {
        VectorStore vectorStore = mock(VectorStore.class);
        ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
        CreditCardProduct gold = CreditCardProduct.builder().productCode("CC_GOLD_003")
                .name("Gold Credit Card").minimumIncome(BigDecimal.valueOf(60000)).build();
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(Document.builder()
                .text("Gold Credit Card has fictional dining benefits.")
                .metadata(java.util.Map.of("cardCode", "CC_GOLD_003")).build()));
        when(chatClientBuilder.build().prompt().system(anyString()).user(anyString()).call().content())
                .thenReturn("Gold Credit Card is eligible based on the supplied profile.");

        RagService service = service(vectorStore, chatClientBuilder, List.of(gold));
        RagChatResponse response = service.chat(new RagChatRequest("Which card is best for me?",
                BigDecimal.valueOf(70000), BigDecimal.valueOf(20000), null, null, null, null, "dining"));

        assertTrue(response.recommendations().stream()
                .anyMatch(recommendation -> recommendation.productCode().equals("CC_GOLD_003")));
    }

    @Test
    void populatesBackendRecommendationsForSuitableCardQuestion() {
        VectorStore vectorStore = mock(VectorStore.class);
        ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class, org.mockito.Answers.RETURNS_DEEP_STUBS);
        CreditCardProduct student = CreditCardProduct.builder().productCode("CC_STUDENT_011")
                .name("Student Credit Card").minimumIncome(BigDecimal.valueOf(150000)).build();
        CreditCardProduct starter = CreditCardProduct.builder().productCode("CC_STARTER_015")
                .name("Starter Credit Card").minimumIncome(BigDecimal.valueOf(200000)).build();
        when(vectorStore.similaritySearch(any(SearchRequest.class))).thenReturn(List.of(Document.builder()
                .text("Student Credit Card has fictional introductory rewards.")
                .metadata(java.util.Map.of("cardCode", "CC_STUDENT_011")).build()));
        when(chatClientBuilder.build().prompt().system(anyString()).user(anyString()).call().content())
                .thenReturn("The Student Credit Card is a practical starting option. This is fictional training data.");

        RagService service = service(vectorStore, chatClientBuilder, List.of(student, starter));
        RagChatResponse response = service.chat(new RagChatRequest(
        "I earn 200000 per month, spend around 50000, "
                + "and most of my spending is on shopping. "
                + "Which card is suitable for me?",
        BigDecimal.valueOf(200000),
        BigDecimal.valueOf(50000),
        null,
        BigDecimal.valueOf(30000),
        null,
        null,
        "shopping"));

        assertFalse(response.recommendations().isEmpty());

        assertTrue(response.recommendations().stream()
        .allMatch(recommendation ->
                recommendation.productCode().equals("CC_STUDENT_011")
                        || recommendation.productCode().equals("CC_STARTER_015")));
    }

    private RagService service(VectorStore vectorStore, ChatClient.Builder chatClientBuilder,
            List<CreditCardProduct> products) {
        CreditCardProductRepository productRepository = mock(CreditCardProductRepository.class);
        CurrentUser currentUser = mock(CurrentUser.class);
        when(currentUser.getUserId()).thenReturn(1L);
        when(productRepository.findByStatusOrderByNameAsc(any())).thenReturn(products);
        RagService service = new RagService(productRepository, vectorStore, chatClientBuilder, currentUser);
        ReflectionTestUtils.setField(service, "topK", 5);
        ReflectionTestUtils.setField(service, "maxRecommendations", 3);
        return service;
    }
}
