package br.com.subscription_processor.application.service;

import br.com.subscription_processor.domain.entity.EventHistory;
import br.com.subscription_processor.domain.entity.Status;
import br.com.subscription_processor.domain.entity.Subscription;
import br.com.subscription_processor.domain.enums.EventType;
import br.com.subscription_processor.domain.repository.EventHistoryRepository;
import br.com.subscription_processor.domain.repository.StatusRepository;
import br.com.subscription_processor.domain.repository.SubscriptionRepository;
import br.com.subscription_processor.exception.StatusNotFoundException;
import br.com.subscription_processor.exception.SubscriptionNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private static final String ACTIVE = "ACTIVE";
    private static final String CANCELED = "CANCELED";

    private final SubscriptionRepository subscriptionRepository;
    private final StatusRepository statusRepository;
    private final EventHistoryRepository eventHistoryRepository;

    @Transactional
    public void processEvent(String subscriptionId, EventType eventType) {
        Status targetStatus = resolveStatus(eventType);
        Subscription subscription = upsertSubscription(subscriptionId, eventType, targetStatus);
        saveEventHistory(subscription, eventType);
    }

    private Status resolveStatus(EventType eventType) {
        String statusName = switch (eventType) {
            case SUBSCRIPTION_PURCHASED, SUBSCRIPTION_RESTARTED -> ACTIVE;
            case SUBSCRIPTION_CANCELED -> CANCELED;
        };

        return statusRepository.findByName(statusName)
                .orElseThrow(() -> new StatusNotFoundException("Status not found: " + statusName));
    }

    private Subscription upsertSubscription(String subscriptionId, EventType eventType, Status targetStatus) {
        LocalDateTime now = LocalDateTime.now();

        return subscriptionRepository.findById(subscriptionId)
                .map(existingSubscription -> {
                    existingSubscription.setStatus(targetStatus);
                    existingSubscription.setUpdatedAt(now);
                    return subscriptionRepository.save(existingSubscription);
                })
                .orElseGet(() -> createSubscriptionIfAllowed(subscriptionId, eventType, targetStatus, now));
    }

    private Subscription createSubscriptionIfAllowed(
            String subscriptionId,
            EventType eventType,
            Status targetStatus,
            LocalDateTime now
    ) {
        if (eventType != EventType.SUBSCRIPTION_PURCHASED) {
            throw new SubscriptionNotFoundException(
                    "Subscription not found for event " + eventType + ": " + subscriptionId
            );
        }

        Subscription subscription = new Subscription();
        subscription.setId(subscriptionId);
        subscription.setStatus(targetStatus);
        subscription.setCreatedAt(now);
        subscription.setUpdatedAt(now);

        return subscriptionRepository.save(subscription);
    }

    private void saveEventHistory(Subscription subscription, EventType eventType) {
        EventHistory eventHistory = new EventHistory();
        eventHistory.setSubscription(subscription);
        eventHistory.setEventType(eventType);
        eventHistory.setProcessedAt(LocalDateTime.now());

        eventHistoryRepository.save(eventHistory);
    }
}