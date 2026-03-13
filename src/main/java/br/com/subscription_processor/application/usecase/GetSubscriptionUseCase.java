package br.com.subscription_processor.application.usecase;

import br.com.subscription_processor.application.dto.response.SubscriptionResponse;
import br.com.subscription_processor.application.mapper.SubscriptionMapper;
import br.com.subscription_processor.domain.repository.SubscriptionRepository;
import br.com.subscription_processor.exception.SubscriptionNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetSubscriptionUseCase {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionResponse execute(String subscriptionId) {
        return subscriptionRepository.findByIdWithStatus(subscriptionId)
                .map(subscriptionMapper::toResponse)
                .orElseThrow(() ->
                        new SubscriptionNotFoundException("Subscription not found: " + subscriptionId));
    }
}