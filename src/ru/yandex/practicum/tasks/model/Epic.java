package ru.yandex.practicum.tasks.model;

import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.util.ArrayList;
import java.util.List;

public class Epic extends BaseTask {
    private List<Integer> subtasksIds = new ArrayList<>();

    public Epic(String name, String description) {
        super(name, description);
    }

    public List<Integer> getSubtasksIds() {
        return new ArrayList<>(subtasksIds);
    }

    public void addSubtask(Subtask subtask) {
        int subtaskId = subtask.getId();

        if (!subtasksIds.contains(subtaskId)) {
            subtasksIds.add(subtaskId);
        }
    }

    public void removeSubtaskById(int subtaskId) {
        int index = subtasksIds.indexOf(subtaskId);
        if (index == -1) {
            return;
        }
        subtasksIds.remove(index);
    }

    public void clearAllSubtaskIds() {
        subtasksIds = new ArrayList<>();
    }


    @Override
    public TaskType getTaskType() {
        return TaskType.EPIC;
    }
}
