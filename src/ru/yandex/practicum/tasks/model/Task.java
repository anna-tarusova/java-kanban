package ru.yandex.practicum.tasks.model;

import ru.yandex.practicum.tasks.model.enums.TaskType;

public class Task extends BaseTask {

    public Task(String name, String description) {
        super(name, description);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.TASK;
    }
}
