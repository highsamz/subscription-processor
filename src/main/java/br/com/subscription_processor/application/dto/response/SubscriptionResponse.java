package br.com.subscription_processor.application.dto.response;

import java.time.LocalDateTime;

public record SubscriptionResponse(
        String id,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}