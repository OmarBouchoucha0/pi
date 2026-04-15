package tn.esprit.pi.service.aichatbot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;
import tn.esprit.pi.dto.aichatbot.ChatRequest;
import tn.esprit.pi.dto.aichatbot.ChatResponse;
import tn.esprit.pi.enums.aichatbot.SenderType;

@Service
@Slf4j
public class AiChatService {

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.model:gpt-4o}")
    private String model;

    @Value("${openai.temperature:0.7}")
    private double temperature;

    @Value("${openai.max-tokens:2048}")
    private int maxTokens;

    private final RestTemplate restTemplate;

    public AiChatService() {
        this.restTemplate = new RestTemplate();
    }

    public String chat(String userMessage, List<tn.esprit.pi.entity.aichatbot.ChatMessage> conversationHistory) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("OpenAI API key not configured. Set openai.api.key property or OPENAI_API_KEY environment variable.");
            return "AI service is not configured. Please contact the administrator to set up the API key.";
        }

        List<tn.esprit.pi.dto.aichatbot.ChatRequest.ChatMessage> messages = buildMessages(userMessage, conversationHistory);

        ChatRequest request = ChatRequest.builder()
                .model(model)
                .messages(messages)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<ChatResponse> response = restTemplate.postForEntity(
                    OPENAI_API_URL, entity, ChatResponse.class);

            if (response.getBody() != null && !response.getBody().getChoices().isEmpty()) {
                return response.getBody().getChoices().get(0).getMessage().getContent();
            }

            return "Sorry, I couldn't generate a response.";

        } catch (Exception e) {
            log.error("Error calling OpenAI API: {}", e.getMessage(), e);
            return "Sorry, an error occurred while generating the response. Please try again later.";
        }
    }

    private List<tn.esprit.pi.dto.aichatbot.ChatRequest.ChatMessage> buildMessages(
            String userMessage,
            List<tn.esprit.pi.entity.aichatbot.ChatMessage> history) {

        List<tn.esprit.pi.dto.aichatbot.ChatRequest.ChatMessage> messages = new ArrayList<>();

        messages.add(tn.esprit.pi.dto.aichatbot.ChatRequest.ChatMessage.builder()
                .role("system")
                .content("You are a helpful medical assistant for a hospital monitoring system. "
                        + "Provide accurate, helpful health information but always remind users to consult "
                        + "a healthcare professional for medical advice. Do not provide specific medical diagnoses.")
                .build());

        if (history != null) {
            for (tn.esprit.pi.entity.aichatbot.ChatMessage msg : history) {
                if (msg.getContent() != null) {
                    messages.add(tn.esprit.pi.dto.aichatbot.ChatRequest.ChatMessage.builder()
                            .role(msg.getSenderType() == SenderType.AI ? "assistant" : "user")
                            .content(msg.getContent())
                            .build());
                }
            }
        }

        messages.add(tn.esprit.pi.dto.aichatbot.ChatRequest.ChatMessage.builder()
                .role("user")
                .content(userMessage)
                .build());

        return messages;
    }

    public boolean isConfigured() {
        return apiKey != null && !apiKey.isBlank();
    }
}
