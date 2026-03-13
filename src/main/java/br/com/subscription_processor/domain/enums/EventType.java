package br.com.subscription_processor.domain.enums;

import br.com.subscription_processor.exception.InvalidEventTypeException;

public enum EventType {

    SUBSCRIPTION_PURCHASED,
    SUBSCRIPTION_CANCELED,
    SUBSCRIPTION_RESTARTED;

    public static EventType from(String value) {
        try {
            return EventType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new InvalidEventTypeException("Invalid event type: " + value);
        }
    }
}