package com.accenture.springai_bootcamp_demo.service;

import com.accenture.springai_bootcamp_demo.dto.ChatMetricsResponse;
import com.accenture.springai_bootcamp_demo.entity.ChatMessage;
import com.accenture.springai_bootcamp_demo.entity.Role;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class ChatMetricsAnalyzer {

    public ChatMetricsResponse analyze(List<ChatMessage> messages) {
        int userMessages = 0;
        int assistantMessages = 0;
        int totalWords = 0;
        String lastUserMessage = "";

        for (ChatMessage message : messages) {
            if (Role.USER.equals(message.getRole())) {
                userMessages++;
                lastUserMessage = message.getContent();
            } else if (Role.ASSISTANT.equals(message.getRole())) {
                assistantMessages++;
            }

            totalWords += countWords(message.getContent());
        }

        return new ChatMetricsResponse(
                messages.size(),
                userMessages,
                assistantMessages,
                totalWords,
                truncate(lastUserMessage)
        );
    }

    private int countWords(String content) {
        if (!StringUtils.hasText(content)) {
            return 0;
        }
        return content.trim().split("\\s+").length;
    }

    private String truncate(String value) {
        if (value == null || value.length() <= 120) {
            return value == null ? "" : value;
        }
        return value.substring(0, 117) + "...";
    }
}
