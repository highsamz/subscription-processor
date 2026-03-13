package br.com.subscription_processor.application.service;

import br.com.subscription_processor.domain.entity.Status;
import br.com.subscription_processor.domain.entity.Subscription;
import br.com.subscription_processor.domain.enums.EventType;
import br.com.subscription_processor.domain.repository.EventHistoryRepository;
import br.com.subscription_processor.domain.repository.StatusRepository;
import br.com.subscription_processor.domain.repository.SubscriptionRepository;
import br.com.subscription_processor.exception.StatusNotFoundException;
import br.com.subscription_processor.exception.SubscriptionNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private EventHistoryRepository eventHistoryRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    private Status activeStatus;
    private Status canceledStatus;

    @BeforeEach
    void setUp() {
        activeStatus = new Status();
        activeStatus.setId(1L);
        activeStatus.setName("ACTIVE");

        canceledStatus = new Status();
        canceledStatus.setId(2L);
        canceledStatus.setName("CANCELED");
    }

    @Test
    void shouldCreateSubscriptionWhenPurchasedAndSubscriptionDoesNotExist() {
        when(statusRepository.findByName("ACTIVE")).thenReturn(Optional.of(activeStatus));
        when(subscriptionRepository.findById("sub_123")).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        subscriptionService.processEvent("sub_123", EventType.SUBSCRIPTION_PURCHASED);

        ArgumentCaptor<Subscription> subscriptionCaptor = ArgumentCaptor.forClass(Subscription.class);
        verify(subscriptionRepository).save(subscriptionCaptor.capture());

        Subscription savedSubscription = subscriptionCaptor.getValue();
        assertEquals("sub_123", savedSubscription.getId());
        assertEquals("ACTIVE", savedSubscription.getStatus().getName());
        assertNotNull(savedSubscription.getCreatedAt());
        assertNotNull(savedSubscription.getUpdatedAt());

        verify(eventHistoryRepository).save(any());
    }

    @Test
    void shouldUpdateSubscriptionToCanceledWhenSubscriptionExists() {
        Subscription existingSubscription = new Subscription();
        existingSubscription.setId("sub_123");
        existingSubscription.setStatus(activeStatus);

        when(statusRepository.findByName("CANCELED")).thenReturn(Optional.of(canceledStatus));
        when(subscriptionRepository.findById("sub_123")).thenReturn(Optional.of(existingSubscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        subscriptionService.processEvent("sub_123", EventType.SUBSCRIPTION_CANCELED);

        assertEquals("CANCELED", existingSubscription.getStatus().getName());
        assertNotNull(existingSubscription.getUpdatedAt());

        verify(subscriptionRepository).save(existingSubscription);
        verify(eventHistoryRepository).save(any());
    }

    @Test
    void shouldUpdateSubscriptionToActiveWhenRestartedAndSubscriptionExists() {
        Subscription existingSubscription = new Subscription();
        existingSubscription.setId("sub_123");
        existingSubscription.setStatus(canceledStatus);

        when(statusRepository.findByName("ACTIVE")).thenReturn(Optional.of(activeStatus));
        when(subscriptionRepository.findById("sub_123")).thenReturn(Optional.of(existingSubscription));
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        subscriptionService.processEvent("sub_123", EventType.SUBSCRIPTION_RESTARTED);

        assertEquals("ACTIVE", existingSubscription.getStatus().getName());

        verify(subscriptionRepository).save(existingSubscription);
        verify(eventHistoryRepository).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCanceledSubscriptionDoesNotExist() {
        when(statusRepository.findByName("CANCELED")).thenReturn(Optional.of(canceledStatus));
        when(subscriptionRepository.findById("sub_456")).thenReturn(Optional.empty());

        SubscriptionNotFoundException exception = assertThrows(
                SubscriptionNotFoundException.class,
                () -> subscriptionService.processEvent("sub_456", EventType.SUBSCRIPTION_CANCELED)
        );

        assertTrue(exception.getMessage().contains("Subscription not found"));

        verify(subscriptionRepository, never()).save(any());
        verify(eventHistoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRestartedSubscriptionDoesNotExist() {
        when(statusRepository.findByName("ACTIVE")).thenReturn(Optional.of(activeStatus));
        when(subscriptionRepository.findById("sub_456")).thenReturn(Optional.empty());

        SubscriptionNotFoundException exception = assertThrows(
                SubscriptionNotFoundException.class,
                () -> subscriptionService.processEvent("sub_456", EventType.SUBSCRIPTION_RESTARTED)
        );

        assertTrue(exception.getMessage().contains("Subscription not found"));

        verify(subscriptionRepository, never()).save(any());
        verify(eventHistoryRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenStatusDoesNotExist() {
        when(statusRepository.findByName("ACTIVE")).thenReturn(Optional.empty());

        StatusNotFoundException exception = assertThrows(
                StatusNotFoundException.class,
                () -> subscriptionService.processEvent("sub_123", EventType.SUBSCRIPTION_PURCHASED)
        );

        assertEquals("Status not found: ACTIVE", exception.getMessage());

        verify(subscriptionRepository, never()).findById(any());
        verify(eventHistoryRepository, never()).save(any());
    }

    @Test
    void shouldPropagateExceptionWhenDatabaseFailsSavingSubscription() {
        when(statusRepository.findByName("ACTIVE")).thenReturn(Optional.of(activeStatus));
        when(subscriptionRepository.findById("sub_123")).thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> subscriptionService.processEvent("sub_123", EventType.SUBSCRIPTION_PURCHASED)
        );

        assertEquals("Database error", exception.getMessage());
        verify(eventHistoryRepository, never()).save(any());
    }
}