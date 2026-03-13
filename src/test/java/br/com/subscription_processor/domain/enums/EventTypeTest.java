package br.com.subscription_processor.domain.enums;

import br.com.subscription_processor.exception.InvalidEventTypeException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTypeTest {

    @Test
    void shouldReturnEventTypeWhenValueIsValid() {
        EventType eventType = EventType.from("SUBSCRIPTION_PURCHASED");

        assertEquals(EventType.SUBSCRIPTION_PURCHASED, eventType);
    }

    @Test
    void shouldThrowExceptionWhenValueIsInvalid() {
        InvalidEventTypeException exception = assertThrows(
                InvalidEventTypeException.class,
                () -> EventType.from("INVALID_EVENT")
        );

        assertEquals("Invalid event type: INVALID_EVENT", exception.getMessage());
    }
}