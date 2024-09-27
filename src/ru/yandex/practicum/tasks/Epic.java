package ru.yandex.practicum.tasks;

import ru.yandex.practicum.tasks.exceptions.Forbidden;

import java.util.ArrayList;
import java.util.List;

public class Epic extends BaseTask {
    private List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description, Status status) {
        super(name, description, status);
    }

    public List<Integer> getSubtasksIds() {
        return subtasksIds;
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void addSubTask(Subtask subtask) {
        subtask.setEpicId(this.id);
        TaskManager.addTask(subtask);
        subtasksIds.add(subtask.getId());
    }

    @Override
    public Status getStatus() {
        List<Subtask> subtasks = TaskManager.getSubtasksOfEpic(id);
        Status epicStatus = Status.IN_PROGRESS;

        if (subtasks.isEmpty() || subtasks
                                    .stream()
                                    .map(BaseTask::getStatus)
                                    .allMatch((Status s) -> s == Status.NEW)) {
            return Status.NEW;
        } else if (subtasks
                    .stream()
                    .map(BaseTask::getStatus)
                    .allMatch((Status s) -> s == Status.DONE)) {
            return Status.DONE;
        }

        return epicStatus;
    }

    @Override
    public void setStatus(Status status) {
        throw new Forbidden("Нельзя вызывать этот метод у Epic");
    }
}
