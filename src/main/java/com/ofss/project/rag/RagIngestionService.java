package com.ofss.project.rag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.rag", name = "enabled", havingValue = "true")
public class RagIngestionService {
    private static final Logger log = LoggerFactory.getLogger(RagIngestionService.class);
    private static final int CHUNK_SIZE = 1_200;
    private static final String VECTOR_TABLE = "CREDPULSE_RAG_VECTORS";

    private final VectorStore vectorStore;
    private final RagMetadataResolver metadataResolver;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public RagIngestionResponse ingest() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath*:rag-data/**/*.txt");
            List<Document> chunks = new ArrayList<>();
            for (Resource resource : resources) {
                String filename = resource.getFilename();
                String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, Object> metadata = metadataResolver.resolve(filename);
                chunks.addAll(chunk(filename, content, metadata));
            }
            if (!chunks.isEmpty()) {
                jdbcTemplate.update("DELETE FROM " + VECTOR_TABLE);
                vectorStore.add(chunks);
            }
            log.info("RAG ingestion completed: {} files, {} chunks indexed", resources.length, chunks.size());
            return new RagIngestionResponse("SUCCESS", resources.length, chunks.size());
        } catch (IOException | RuntimeException ex) {
            log.error("RAG ingestion failed", ex);
            throw new RagUnavailableException("RAG vector indexing is unavailable", ex);
        }
    }

    private List<Document> chunk(String filename, String content, Map<String, Object> baseMetadata) {
        List<Document> chunks = new ArrayList<>();
        for (int start = 0, index = 0; start < content.length(); start += CHUNK_SIZE, index++) {
            int end = Math.min(content.length(), start + CHUNK_SIZE);
            Map<String, Object> metadata = new java.util.HashMap<>(baseMetadata);
            metadata.put("chunkIndex", index);
            String id = UUID.nameUUIDFromBytes((filename + ":" + index).getBytes(StandardCharsets.UTF_8)).toString();
            chunks.add(Document.builder().id(id).text(content.substring(start, end)).metadata(metadata).build());
        }
        return chunks;
    }
}
