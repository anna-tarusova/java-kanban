package ru.yandex.practicum.tasks.exceptions;

public class Forbidden extends RuntimeException {
    public Forbidden(String message) {
        super(message);
    }
}
