package br.com.subscription_processor.application.usecase;

import br.com.subscription_processor.application.service.SubscriptionService;
import br.com.subscription_processor.domain.enums.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessNotificationUseCase {

    private final SubscriptionService subscriptionService;

    public void execute(String subscriptionId, EventType eventType) {
        log.info("Processando subscriptionId={} com eventType={}", subscriptionId, eventType);
        subscriptionService.processEvent(subscriptionId, eventType);
    }
}