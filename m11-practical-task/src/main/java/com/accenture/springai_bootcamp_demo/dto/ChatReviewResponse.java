package com.accenture.springai_bootcamp_demo.dto;

public record ChatReviewResponse(
        ChatMetricsResponse metrics,
        String summary,
        String recommendations
) {
}
