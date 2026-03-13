package br.com.subscription_processor.exception;

public class InvalidEventTypeException extends RuntimeException {

    public InvalidEventTypeException(String message) {
        super(message);
    }
}