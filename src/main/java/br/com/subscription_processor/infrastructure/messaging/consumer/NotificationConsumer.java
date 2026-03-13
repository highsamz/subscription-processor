package br.com.subscription_processor.infrastructure.messaging.consumer;

import br.com.subscription_processor.application.dto.message.NotificationEventMessage;
import br.com.subscription_processor.application.usecase.ProcessNotificationUseCase;
import br.com.subscription_processor.config.RabbitMQConfig;
import br.com.subscription_processor.domain.enums.EventType;
import br.com.subscription_processor.exception.InvalidEventTypeException;
import br.com.subscription_processor.exception.StatusNotFoundException;
import br.com.subscription_processor.exception.SubscriptionNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final ProcessNotificationUseCase processNotificationUseCase;

    @RabbitListener(queues = RabbitMQConfig.SUBSCRIPTION_QUEUE)
    public void consume(NotificationEventMessage message) {
        log.info("Mensagem recebida da fila: {}", message);

        try {
            EventType eventType = EventType.from(message.eventType());
            processNotificationUseCase.execute(message.subscriptionId(), eventType);
        } catch (InvalidEventTypeException
                 | StatusNotFoundException
                 | SubscriptionNotFoundException ex) {

            log.error("Erro de negócio ao processar mensagem {}: {}", message, ex.getMessage());
        }
    }
}