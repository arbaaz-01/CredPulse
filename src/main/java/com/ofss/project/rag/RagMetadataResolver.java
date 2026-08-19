package com.ofss.project.rag;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class RagMetadataResolver {

    private static final Map<String, CardMetadata> CARD_FILES = Map.ofEntries(
            Map.entry("basic-credit-card.txt", new CardMetadata("CC_BASIC_001", "Basic Credit Card")),
            Map.entry("silver-credit-card.txt", new CardMetadata("CC_SILVER_002", "Silver Credit Card")),
            Map.entry("gold-credit-card.txt", new CardMetadata("CC_GOLD_003", "Gold Credit Card")),
            Map.entry("platinum-credit-card.txt", new CardMetadata("CC_PLATINUM_004", "Platinum Credit Card")),
            Map.entry("travel-rewards-card.txt", new CardMetadata("CC_TRAVEL_005", "Travel Rewards Card")),
            Map.entry("fuel-rewards-card.txt", new CardMetadata("CC_FUEL_006", "Fuel Rewards Card")),
            Map.entry("cashback-credit-card.txt", new CardMetadata("CC_CASHBACK_007", "Cashback Credit Card")),
            Map.entry("shopping-rewards-card.txt", new CardMetadata("CC_SHOP_008", "Shopping Rewards Card")),
            Map.entry("dining-rewards-card.txt", new CardMetadata("CC_DINING_009", "Dining Rewards Card")),
            Map.entry("corporate-credit-card.txt", new CardMetadata("CC_CORPORATE_010", "Corporate Credit Card")),
            Map.entry("student-credit-card.txt", new CardMetadata("CC_STUDENT_011", "Student Credit Card")),
            Map.entry("premium-rewards-card.txt", new CardMetadata("CC_PREMIUM_012", "Premium Rewards Card")),
            Map.entry("lifestyle-credit-card.txt", new CardMetadata("CC_LIFESTYLE_013", "Lifestyle Credit Card")),
            Map.entry("elite-credit-card.txt", new CardMetadata("CC_ELITE_014", "Elite Credit Card")),
            Map.entry("starter-credit-card.txt", new CardMetadata("CC_STARTER_015", "Starter Credit Card")));

    public Map<String, Object> resolve(String filename) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("source", filename);
        CardMetadata card = CARD_FILES.get(filename);
        if (card != null) {
            metadata.put("category", "card");
            metadata.put("cardCode", card.code());
            metadata.put("cardName", card.name());
            metadata.put("documentType", "card_benefits");
        } else if (filename.contains("eligibility")) {
            metadata.put("category", "policy");
            metadata.put("documentType", "eligibility");
        } else if (filename.contains("approval")) {
            metadata.put("category", "policy");
            metadata.put("documentType", "approval");
        } else {
            metadata.put("category", "rewards");
            metadata.put("documentType", filename.contains("cashback") ? "cashback_rules" : "reward_rules");
        }
        return metadata;
    }

    private record CardMetadata(String code, String name) {
    }
}
