package com.ofss.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.ai.google.genai.embedding.api-key=test-key")
class ProjectApplicationTests {

	@Test
	void contextLoads() {
	}

}
