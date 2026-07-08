package com.accenture.springai_bootcamp_demo.dto;

public record ChatMetricsResponse(
        int totalMessages,
        int userMessages,
        int assistantMessages,
        int totalWords,
        String lastUserMessage
) {
}
