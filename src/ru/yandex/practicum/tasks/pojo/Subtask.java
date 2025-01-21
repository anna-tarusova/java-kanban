package ru.yandex.practicum.tasks.pojo;

import ru.yandex.practicum.tasks.model.enums.Status;

import java.time.LocalDateTime;
import java.util.OptionalInt;

public class Subtask {
    private final String name;
    private final String description;
    private OptionalInt id;
    private final int epicId;
    private final Integer duration;
    private final LocalDateTime startTime;
    private final Status status;

    public Subtask(String name, String description, OptionalInt id, int epicId, Integer duration, LocalDateTime startTime, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.epicId = epicId;
        this.duration = duration;
        this.startTime = startTime;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public OptionalInt getId() {
        return id;
    }

    public int getEpicId() {
        return epicId;
    }

    public Integer getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Status getStatus() {
        return status;
    }
}
