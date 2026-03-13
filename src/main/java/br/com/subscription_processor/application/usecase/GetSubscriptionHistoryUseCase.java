package br.com.subscription_processor.application.usecase;

import br.com.subscription_processor.application.dto.response.EventHistoryResponse;
import br.com.subscription_processor.application.mapper.EventHistoryMapper;
import br.com.subscription_processor.domain.repository.EventHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GetSubscriptionHistoryUseCase {

    private final EventHistoryRepository eventHistoryRepository;
    private final EventHistoryMapper eventHistoryMapper;

    public Page<EventHistoryResponse> execute(String subscriptionId, Pageable pageable) {
        return eventHistoryRepository
                .findBySubscriptionIdOrderByProcessedAtAsc(subscriptionId, pageable)
                .map(eventHistoryMapper::toResponse);
    }
}