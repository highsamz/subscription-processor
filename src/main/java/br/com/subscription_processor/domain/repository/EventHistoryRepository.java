package br.com.subscription_processor.domain.repository;

import br.com.subscription_processor.domain.entity.EventHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventHistoryRepository extends JpaRepository<EventHistory, Long> {

    Page<EventHistory> findBySubscriptionIdOrderByProcessedAtAsc(String subscriptionId, Pageable pageable);
}