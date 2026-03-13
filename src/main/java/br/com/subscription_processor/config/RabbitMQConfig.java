package br.com.subscription_processor.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String SUBSCRIPTION_QUEUE = "subscription.notification.queue";

    @Bean
    public Queue subscriptionQueue() {
        return new Queue(SUBSCRIPTION_QUEUE, true);
    }
}
