package ru.yandex.practicum.tasks.model;

import ru.yandex.practicum.tasks.model.enums.TaskType;

public class Subtask extends BaseTask {

    private int epicId;

    @Override
    public String toString() {
        return String.format("%s,%d", super.toString(), epicId);
    }

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
}

