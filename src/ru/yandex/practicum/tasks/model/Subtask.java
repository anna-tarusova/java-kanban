package ru.yandex.practicum.tasks.model;

import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.util.Objects;

public class Subtask extends BaseTask {

    private int epicId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.SUBTASK;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId && id == subtask.id && Objects.equals(name, subtask.name) && Objects.equals(description, subtask.description) && status == subtask.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}