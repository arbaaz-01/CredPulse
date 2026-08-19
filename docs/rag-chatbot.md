# CredPulse RAG chatbot

The RAG chatbot uses the existing Oracle datasource for both relational product eligibility and Oracle AI Vector Search. `CREDIT_CARD_PRODUCTS` remains the structured source of truth. The vector table `CREDPULSE_RAG_VECTORS` only stores fictional product, policy, and reward knowledge.

## Setup

Oracle Database Free 26ai is installed locally and supports Oracle AI Vector Search. The configured service is `FREEPDB1`, and the configured schema is `project`. Set `GEMINI_API_KEY` and enable the feature before starting the application:

```powershell
$env:GEMINI_API_KEY = "your-key"
$env:RAG_ENABLED = "true"
$env:SPRING_AI_MODEL_CHAT = "google-genai"
$env:SPRING_AI_MODEL_EMBEDDING_TEXT = "google-genai"
$env:SPRING_AI_VECTORSTORE_TYPE = "oracle"
```

The default embedding model is Gemini `gemini-embedding-001` (768 dimensions), which matches `GEMINI_EMBEDDING_DIMENSIONS`. Change both together if a different embedding model is selected. The schema is initialized by Spring AI; the database user needs permission to create and use the vector table and index.

## Ingestion

`POST /api/v1/rag/admin/ingest`

Headers: `Authorization: Bearer <admin-jwt>` and `Content-Type: application/json`.

The endpoint is admin-only. It reads `src/main/resources/rag-data`, chunks files, applies metadata, removes prior deterministic chunk IDs, and inserts replacement embeddings.

```json
{"status":"SUCCESS","documentsProcessed":14,"chunksIndexed":14}
```

## Chat

`POST /api/v1/rag/chat`

Headers: `Authorization: Bearer <user-jwt>` and `Content-Type: application/json`.

```json
{
  "question": "What type of card is good for me if my expenses are 20k per month and income is 70k?",
  "income": 70000,
  "monthlyExpense": 20000,
  "shoppingExpense": 5000,
  "preferredBenefit": "cashback"
}
```

The response contains the question, retrieval-ranked eligible product recommendations, an LLM-generated grounded answer, and a fictional-training disclaimer. A missing question returns `400`; disabled or unavailable AI/vector infrastructure returns `503`; authentication and admin authorization follow the existing JWT security rules.

```powershell
curl.exe -X POST http://localhost:8080/api/v1/rag/chat -H "Authorization: Bearer <user-jwt>" -H "Content-Type: application/json" -d "{\"question\":\"Which card is best for travel?\",\"income\":70000,\"monthlyExpense\":25000}"
```

## Safety and operation

The API never accepts a user ID. It uses normal JWT authentication and does not put passwords, tokens, CVVs, full card numbers, or credentials into vector documents. AI does not query arbitrary relational tables; eligible card candidates are calculated by the application before context is sent to the model.
