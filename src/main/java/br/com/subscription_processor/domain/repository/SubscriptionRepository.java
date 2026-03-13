package br.com.subscription_processor.domain.repository;

import br.com.subscription_processor.domain.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {

    @Query("""
            SELECT s
            FROM Subscription s
            JOIN FETCH s.status
            WHERE s.id = :id
            """)
    Optional<Subscription> findByIdWithStatus(String id);
}