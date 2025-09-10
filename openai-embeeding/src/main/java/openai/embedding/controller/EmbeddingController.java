package openai.embedding.controller;

import java.util.List;
import java.util.Map;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: xzh
 * @Date: 2025-09-09
 */

@RestController
public class EmbeddingController {

	private final EmbeddingModel embeddingModel;

	@Autowired
	public EmbeddingController(EmbeddingModel embeddingModel) {
		this.embeddingModel = embeddingModel;
	}

	@GetMapping("/simple/embedding")
	public Map simple(@RequestParam(value = "message", defaultValue = "体恤衫") String message) {
		EmbeddingResponse embeddingResponse = this.embeddingModel.embedForResponse(List.of(message));
		return Map.of("embedding", embeddingResponse);
	}

	@GetMapping("/custom1/embedding")
	public Map custom() {
		EmbeddingResponse embeddingResponse = embeddingModel
				.call(new EmbeddingRequest(List.of("Hello World", "World is big and salvation is near"),
						OpenAiEmbeddingOptions.builder().model("Qwen3-Embedding-8B").build()));
		return Map.of("embedding", embeddingResponse);
	}
}
