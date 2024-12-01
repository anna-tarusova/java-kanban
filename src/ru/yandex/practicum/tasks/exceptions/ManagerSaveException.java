package ru.yandex.practicum.tasks.exceptions;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }
}
