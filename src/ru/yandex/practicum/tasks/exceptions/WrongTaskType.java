package ru.yandex.practicum.tasks.exceptions;

public class WrongTaskType extends RuntimeException {
    public WrongTaskType(String message) {
        super(message);
    }
}
