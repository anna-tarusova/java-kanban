package ru.yandex.practicum.tasks;

public class Task extends BaseTask {

    public Task(String name, String description, Status status) {
        super(name, description, status);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.TASK;
    }
}
