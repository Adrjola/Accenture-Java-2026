package com.accenture.springai_bootcamp_demo.service;

import com.accenture.springai_bootcamp_demo.client.AiClient;
import com.accenture.springai_bootcamp_demo.dto.ChatMetricsResponse;
import com.accenture.springai_bootcamp_demo.dto.ChatReviewResponse;
import com.accenture.springai_bootcamp_demo.entity.ChatMessage;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ChatReviewWorkflow {

    private final ChatMetricsAnalyzer metricsAnalyzer;
    private final AiClient aiClient;

    public ChatReviewResponse review(List<ChatMessage> messages) {
        ChatMetricsResponse metrics = metricsAnalyzer.analyze(messages);
        String summary = aiClient.summarize(messages);
        String recommendations = aiClient.recommendNextSteps(summary, metrics);

        return new ChatReviewResponse(metrics, summary, recommendations);
    }
}
