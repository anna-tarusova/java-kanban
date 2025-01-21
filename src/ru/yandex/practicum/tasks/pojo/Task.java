package ru.yandex.practicum.tasks.pojo;

import ru.yandex.practicum.tasks.model.enums.Status;

import java.time.LocalDateTime;
import java.util.OptionalInt;

public class Task {

    private final String name;
    private final String description;
    private final Integer duration;
    private final LocalDateTime startTime;
    private OptionalInt id;
    private final Status status;

    public Task(String name, String description, Integer duration, LocalDateTime startTime, OptionalInt id, Status status) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.id = id;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public OptionalInt getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }
}
