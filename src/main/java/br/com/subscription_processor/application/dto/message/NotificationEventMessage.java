package br.com.subscription_processor.application.dto.message;

public record NotificationEventMessage(
        String subscriptionId,
        String eventType
) {
}