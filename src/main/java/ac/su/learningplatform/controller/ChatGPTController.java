package ac.su.learningplatform.controller;

import ac.su.learningplatform.domain.ChatGPTRequest;
import ac.su.learningplatform.domain.ChatGPTResponse;
import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/chat")
public class ChatGPTController {

    @Value("${openai.model}")
    private String model;

    @Value("${openai.api.url}")
    private String apiURL;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Cache<String, String> gptResponseCache;

    @PostMapping("/message")
    public Mono<String> getChatResponse(@RequestBody String userInput) {
        return processChatResponse(userInput);
    }

    @GetMapping("/message")
    public Mono<String> getChatResponseUsingGet(@RequestParam String prompt) {
        return processChatResponse(prompt);
    }

    private Mono<String> processChatResponse(String userInput) {
        // 캐시에서 응답 확인
        String cachedResponse = gptResponseCache.getIfPresent(userInput);
        if (cachedResponse != null) {
            return Mono.just(cachedResponse);
        }

        // 캐시에 응답이 없으면 API 호출
        ChatGPTRequest request = new ChatGPTRequest(model, userInput);
        ChatGPTResponse gptResponse = restTemplate.postForObject(apiURL, request, ChatGPTResponse.class);

        // 응답이 null이 아닌지 확인하고 처리
        if (gptResponse != null && gptResponse.getChoices() != null && !gptResponse.getChoices().isEmpty()) {
            String responseContent = gptResponse.getChoices().get(0).getMessage().getContent();

            // 응답을 캐시에 저장
            gptResponseCache.put(userInput, responseContent);

            return Mono.just(responseContent);
        }

        return Mono.just("Error: No response from API");
    }
}
