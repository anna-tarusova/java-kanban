package ru.yandex.practicum.tasks.logic;

import ru.yandex.practicum.tasks.exceptions.WrongTaskTypeException;
import ru.yandex.practicum.tasks.model.BaseTask;
import ru.yandex.practicum.tasks.model.Epic;
import ru.yandex.practicum.tasks.model.Subtask;
import ru.yandex.practicum.tasks.model.Task;
import ru.yandex.practicum.tasks.model.enums.TaskType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    public static final int MAX_HISTORY_SIZE = 10;
    private List<BaseTask> history = new LinkedList<>();

    @Override
    public void add(BaseTask task) {
        BaseTask copyTask = null;
        if (task.getTaskType() == TaskType.TASK) {
            copyTask = new Task(task.getName(), task.getDescription());
        }
        else if (task.getTaskType() == TaskType.SUBTASK) {
            copyTask = new Subtask(task.getName(), task.getDescription());
        }
        else if (task.getTaskType() == TaskType.EPIC) {
            copyTask = new Epic(task.getName(), task.getDescription());
        }

        if (copyTask == null) {
            throw new WrongTaskTypeException("Неизвестный тип таски");
        }

        copyTask.setId(task.getId());
        copyTask.setStatus(task.getStatus());

        if (history.size() == MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
        history.addLast(copyTask);
    }

    @Override
    public List<BaseTask> getHistory() {
        return new ArrayList<>(history);
    }
}
