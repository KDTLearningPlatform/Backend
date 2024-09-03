package ac.su.learningplatform.service;

import ac.su.learningplatform.domain.ChatGPTRequest;
import ac.su.learningplatform.domain.ChatGPTResponse;
import ac.su.learningplatform.domain.ChatLog;
import ac.su.learningplatform.repository.ChatLogRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ChatGPTService {

    private final WebClient webClient;
    private final ChatLogRepository chatLogRepository;

    @Value("${openai.api.key}")
    private String apiKey;

    public ChatGPTService(WebClient.Builder webClientBuilder, ChatLogRepository chatLogRepository) {
        this.webClient = webClientBuilder.baseUrl("https://api.openai.com/v1").build();
        this.chatLogRepository = chatLogRepository;
    }

    public Mono<String> getChatResponse(String userInput) {
        String prompt = "User: " + userInput + "\nAI:";

        return this.webClient.post()
                .uri("/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(new ChatGPTRequest("gpt-3.5-turbo", prompt))
                .retrieve()
                .bodyToMono(ChatGPTResponse.class)
                .map(response -> {
                    String gptResponse = response.getChoices().get(0).getMessage().getContent().trim();

                    // 프롬프트와 응답을 데이터베이스에 저장
                    ChatLog chatLog = new ChatLog(prompt, gptResponse);
                    chatLogRepository.save(chatLog);

                    return gptResponse;
                })
                .doOnError(error -> {
                    // WebClient 호출 중 발생한 오류를 로그로 출력
                    System.err.println("Error during ChatGPT API call: " + error.getMessage());
                });
    }
}
