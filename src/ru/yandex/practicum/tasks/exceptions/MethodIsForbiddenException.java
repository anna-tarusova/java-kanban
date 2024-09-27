package ru.yandex.practicum.tasks.exceptions;

public class MethodIsForbiddenException extends RuntimeException {
    public MethodIsForbiddenException(String message) {
        super(message);
    }
}
