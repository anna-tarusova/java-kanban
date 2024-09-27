package ru.yandex.practicum.tasks.exceptions;

public class WrongTaskTypeException extends RuntimeException {
    public WrongTaskTypeException(String message) {
        super(message);
    }
}
