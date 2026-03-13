package br.com.subscription_processor.infrastructure.web.controller;

import br.com.subscription_processor.application.dto.response.EventHistoryResponse;
import br.com.subscription_processor.application.dto.response.SubscriptionResponse;
import br.com.subscription_processor.application.usecase.GetSubscriptionHistoryUseCase;
import br.com.subscription_processor.application.usecase.GetSubscriptionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final GetSubscriptionUseCase getSubscriptionUseCase;
    private final GetSubscriptionHistoryUseCase getSubscriptionHistoryUseCase;

    @GetMapping("/{id}")
    public SubscriptionResponse getById(@PathVariable String id) {
        return getSubscriptionUseCase.execute(id);
    }

    @GetMapping("/{id}/history")
    public Page<EventHistoryResponse> getHistory(@PathVariable String id, Pageable pageable) {
        return getSubscriptionHistoryUseCase.execute(id, pageable);
    }
}