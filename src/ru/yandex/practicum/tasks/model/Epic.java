package ru.yandex.practicum.tasks.model;

import ru.yandex.practicum.tasks.model.enums.TaskType;

public class Epic extends BaseTask {

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }
}
