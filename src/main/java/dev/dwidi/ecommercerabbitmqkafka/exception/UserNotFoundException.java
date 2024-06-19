package dev.dwidi.ecommercerabbitmqkafka.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}
