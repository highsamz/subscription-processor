package br.com.subscription_processor.application.dto.response;

import java.time.LocalDateTime;

public record EventHistoryResponse(
        Long id,
        String eventType,
        LocalDateTime processedAt
) {
}