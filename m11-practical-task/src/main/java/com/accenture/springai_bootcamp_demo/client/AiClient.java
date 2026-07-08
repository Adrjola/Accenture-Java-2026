package com.accenture.springai_bootcamp_demo.client;

import com.accenture.springai_bootcamp_demo.entity.ChatMessage;
import com.accenture.springai_bootcamp_demo.entity.Role;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
public class AiClient {

    private static final String SYSTEM_PROMPT = """
            You are a helpful assistant inside a Spring Boot chat application.
            Answer clearly and concisely. Do not include hidden reasoning or <think> tags.
            """;

    private final ChatClient chatClient;

    public AiClient(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String complete(List<ChatMessage> history) {
        String prompt = """
                Continue this conversation as the assistant.

                Conversation:
                %s
                """.formatted(formatHistory(history));
        return call(prompt);
    }

    public String summarize(List<ChatMessage> history) {
        if (history.isEmpty()) {
            return "This chat has no messages yet.";
        }

        String prompt = """
                Summarize this conversation in 3 to 5 short bullet points.

                Conversation:
                %s
                """.formatted(formatHistory(history));
        return call(prompt);
    }

    private String call(String prompt) {
        try {
            String content = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(prompt)
                    .call()
                    .content();

            return extractContent(content);
        } catch (AiException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            log.error("AI request failed", ex);
            throw new AiException("Failed to get AI response: " + ex.getMessage(), ex);
        }
    }

    private String formatHistory(List<ChatMessage> history) {
        if (history.isEmpty()) {
            return "No previous messages.";
        }

        StringBuilder builder = new StringBuilder();
        for (ChatMessage message : history) {
            builder.append(labelFor(message))
                    .append(": ")
                    .append(message.getContent())
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    private String labelFor(ChatMessage message) {
        return Role.USER.equals(message.getRole()) ? "User" : "Assistant";
    }

    private String extractContent(String content) {
        if (!StringUtils.hasText(content)) {
            throw new AiException("AI model returned an empty response");
        }

        String cleaned = content.replaceAll("(?s)<think>.*?</think>", "").trim();
        if (!StringUtils.hasText(cleaned)) {
            throw new AiException("AI model returned an empty response");
        }

        return cleaned;
    }
}
