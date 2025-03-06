package net.xzh.struct.controller;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import net.xzh.struct.model.StreamToBeanEntity;
import reactor.core.publisher.Flux;

/**
 * @author Carbon
 */
@RestController
@RequestMapping("/example/stream")
public class StreamToBeanController {

	private final ChatClient chatClient;

	private static final Logger log = LoggerFactory.getLogger(StreamToBeanController.class);

	public StreamToBeanController(ChatClient.Builder builder) {
		this.chatClient = builder.build();
	}

	/**
	 * @return {@link com.alibaba.cloud.ai.example.outparser.stream.StreamToBeanEntity}
	 */
	@GetMapping("/play")
	public StreamToBeanEntity simpleChat(HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		var converter = new BeanOutputConverter<>(
				new ParameterizedTypeReference<StreamToBeanEntity>() { }
		);
		Flux<String> flux = this.chatClient.prompt()
				.user(u -> u.text("""
						requirement: 请用大概 120 字，作者为 牧生 ，为计算机的发展历史写一首现代诗;
						format: 以纯文本输出 json，请不要包含任何多余的文字——包括 markdown 格式;
						outputExample: {
							 "title": {title},
							 "author": {author},
							 "date": {date},
							 "content": {content}
						};
						"""))
				.stream()
				.content();

		String result = String.join("\n", Objects.requireNonNull(flux.collectList().block()))
				.replaceAll("\\n", "")
				.replaceAll("\\s+", " ")
				.replaceAll("\"\\s*:", "\":")
				.replaceAll(":\\s*\"", ":\"");

		log.info("LLMs 响应的 json 数据为：{}", result);
		return converter.convert(result);
	}

}
