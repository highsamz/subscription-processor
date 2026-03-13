package br.com.subscription_processor.infrastructure.messaging.consumer;

import br.com.subscription_processor.application.dto.message.NotificationEventMessage;
import br.com.subscription_processor.application.usecase.ProcessNotificationUseCase;
import br.com.subscription_processor.domain.enums.EventType;
import br.com.subscription_processor.exception.SubscriptionNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class NotificationConsumerTest {

    @Mock
    private ProcessNotificationUseCase processNotificationUseCase;

    @InjectMocks
    private NotificationConsumer notificationConsumer;

    @Test
    void shouldCallUseCaseWhenMessageIsValid() {
        NotificationEventMessage message = new NotificationEventMessage(
                "sub_123",
                "SUBSCRIPTION_PURCHASED"
        );

        notificationConsumer.consume(message);

        verify(processNotificationUseCase)
                .execute("sub_123", EventType.SUBSCRIPTION_PURCHASED);
    }

    @Test
    void shouldNotPropagateBusinessException() {
        NotificationEventMessage message = new NotificationEventMessage(
                "sub_456",
                "SUBSCRIPTION_CANCELED"
        );

        doThrow(new SubscriptionNotFoundException("Subscription not found"))
                .when(processNotificationUseCase)
                .execute("sub_456", EventType.SUBSCRIPTION_CANCELED);

        assertDoesNotThrow(() -> notificationConsumer.consume(message));
    }
}