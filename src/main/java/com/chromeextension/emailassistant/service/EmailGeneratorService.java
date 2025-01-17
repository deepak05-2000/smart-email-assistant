package com.chromeextension.emailassistant.service;

import com.chromeextension.emailassistant.data.EmailBody;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGeneratorService {

    private static final Logger log = LoggerFactory.getLogger(EmailGeneratorService.class);
    WebClient webClient;
    @Value("${gemini.api.url}")
    private String geminiAPIUrl;
    @Value("${gemini.api.key}")
    private String API_KEY;

    public EmailGeneratorService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String generateEmailReply(EmailBody emailBody) {
        String prompt = buildPrompt(emailBody);
        Map<String, Object> requestBody = Map.of("contents",
                new Object[]{Map.of("parts", new Object[]{Map.of("text", prompt)})}
        );

        String response = webClient.post()
                                   .uri(geminiAPIUrl + API_KEY)
                                   .header("Content-Type", "application/json")
                                   .bodyValue(requestBody)
                                   .retrieve()
                                   .bodyToMono(String.class)
                                   .block();

        return extractResponseContent(response);

    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            return rootNode.path("candidates").get(0).path("content").path("parts").get(0).path("text").asText();

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildPrompt(EmailBody emailBody) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a Professional email reply for the email content." + "Ignore Subject");
        if (emailBody.getTone()!=null && !emailBody.getTone().isEmpty()) {
            prompt.append("Use a ").append(emailBody.getTone()).append(" tone.");
        }
        prompt.append("\nOriginal email: ").append(emailBody.getEmailContent());
        return prompt.toString();
    }
}
