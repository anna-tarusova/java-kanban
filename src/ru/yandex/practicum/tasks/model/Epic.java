package ru.yandex.practicum.tasks.model;

import ru.yandex.practicum.tasks.exceptions.MethodIsForbiddenException;
import ru.yandex.practicum.tasks.model.enums.Status;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends BaseTask {

    private LocalDateTime endTime;
    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    private void calculateDuration() {
        if (subtasks.stream().allMatch(s -> s.getDuration() == null)) {
            duration = null;
        }
        else {
            duration = subtasks.stream()
                    .map(BaseTask::getDuration)
                    .filter(Objects::nonNull)
                    .reduce(Duration.ZERO, (accDuration, currentDuration) -> currentDuration.plus(accDuration), (accDuration, _) -> accDuration);
        }
    }

    private void calculateStartTime() {
        startTime = subtasks.stream()
                .map(BaseTask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
    }

    private void calculateEndTime() {
        endTime = subtasks.stream()
                .map(BaseTask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(null);
    }

    public void calculateStatus() {
        if (subtasks.stream().allMatch(t -> t.getStatus() == Status.NEW)) {
            status = Status.NEW;
        } else if (subtasks.stream().allMatch(t -> t.getStatus() == Status.DONE)) {
            status = Status.DONE;
        } else {
            status = Status.IN_PROGRESS;
        }
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }

    public void addSubtask(Subtask subtask) {
        if (subtasks.stream().anyMatch(s -> s.getId() == subtask.getId())) {
            throw new IllegalStateException("Нельзя добавить сабтаску дважды");
        }
        if (subtask.getEpicId() != id) {
            throw new IllegalStateException("Нельзя добавить сабтаску от другого эпика");
        }

        long overlappedCount = subtasks.stream()
                .filter(s -> BaseTask.areTimeSpansOverLapped(s.getStartTime(), s.getEndTime(), subtask.getStartTime(), subtask.getEndTime())).count();
        if (overlappedCount > 0) {
            throw new IllegalStateException("Есть пересечение с уже существующими тасками");
        }

        subtasks.add(subtask);

        calculateAll();
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        throw new MethodIsForbiddenException("Этот метод нельзя вызывать для эпика");
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public void setDuration(Duration duration) {
        throw new MethodIsForbiddenException("Этот метод нельзя вызывать для эпика");
    }

    public void removeSubtask(int id) {
        subtasks = subtasks.stream().filter(s -> s.getId() != id).toList();
        calculateAll();
    }

    @Override
    public void setStatus(Status status) {
        throw new MethodIsForbiddenException("Этот метод нельзя вызывать для эпика");
    }

    public void calculateAll() {
        calculateDuration();
        calculateStartTime();
        calculateEndTime();
        calculateStatus();
    }
}
